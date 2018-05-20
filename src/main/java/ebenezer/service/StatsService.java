package ebenezer.service;

import ebenezer.dao.StatsMetadataDao;
import ebenezer.dao.StatsRecordDao;
import ebenezer.model.StatsMetadata;
import ebenezer.model.StatsRecord;
import ebenezer.model.User;
import ebenezer.permissions.SitePermission;
import ebenezer.rest.NoPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StatsService {
    private static final Logger LOG = LoggerFactory.getLogger(StatsService.class);
    public static final long STATS_RETENTION_DAYS = 30;

    public static final String USER = "USER";

    @Inject
    private StatsRecordDao statsRecordDao;
    @Inject
    private StatsMetadataDao statsMetadataDao;
    @Inject
    private UserService userService;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private ExecutorService statsExecutorService;

    public StatsService() {
        statsExecutorService = Executors.newSingleThreadExecutor();
    }

    public boolean logStats(String key, Map<String, String> metadata) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            metadata.put("USER", String.valueOf(currentUser.get().getId()));
        }

        statsExecutorService.submit(new StatsJob(key, metadata, entityManagerFactory));

        return true;
    }

    public List<StatsRecord> getStats(Date startDate, Date endDate) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && SitePermissionService.userHasPermission(currentUser.get(), SitePermission.VIEW_STATISTICS)) {
            return statsRecordDao.findByDateRange(startDate, endDate);
        }

        throw new NoPermissionException("You do not have permission to view statistics records");
    }

    private class StatsJob implements Runnable {
        private String key;
        private Map<String, String> metadata;
        private EntityManagerFactory entityManagerFactory;

        public StatsJob(String key, Map<String, String> metadata, EntityManagerFactory entityManagerFactory) {
            this.key = key;
            this.metadata = metadata;
            this.entityManagerFactory = entityManagerFactory;
        }

        @Override
        public void run() {
            try {
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();

                StatsRecord statsRecord = new StatsRecord(key);
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    StatsMetadata statsMetadata = new StatsMetadata(entry.getKey(), entry.getValue(), statsRecord);
                    statsRecord.getMetadata().add(statsMetadata);
                }
                entityManager.persist(statsRecord);
                transaction.commit();

                // Clean up
                Date oldestRecord = new Date(System.currentTimeMillis() - (STATS_RETENTION_DAYS * 24L * 60L * 60L * 1000L));

                transaction.begin();
                Query query = entityManager.createQuery(
                        "delete from StatsRecord stats where stats.created < :oldest");
                query.setParameter("oldest", oldestRecord.getTime());
                int rows = query.executeUpdate();
                if (rows > 0) {
                    LOG.info("Deleted " + rows + " old stats records");
                    transaction.commit();
                } else {
                    transaction.rollback();
                }

                entityManager.close();
            } catch (Exception e) {
                LOG.error("Failed to log stats", e);
            }
        }
    }
}
