package kc.ebenezer.service;

import kc.ebenezer.dao.AuditRecordDao;
import kc.ebenezer.model.AuditLevel;
import kc.ebenezer.model.AuditRecord;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.User;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.exception.NoPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class AuditService {
    private static final Long AUDIT_RETENTION_DAYS = 7L;

    private static final Logger LOG = LoggerFactory.getLogger(AuditService.class);

    @Inject
    private AuditRecordDao auditRecordDao;

    @Inject
    private UserService userService;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private ExecutorService auditingService;

    public AuditService() {
        auditingService = Executors.newSingleThreadExecutor();
    }

    public boolean audit(Project project, String change, Date changeTime, AuditLevel auditLevel) {
        Optional<User> currentUser = userService.getCurrentUser();

        auditingService.submit(new AuditJob(change, changeTime, currentUser, Optional.ofNullable(project), auditLevel, entityManagerFactory));
        return true;
    }

    public boolean audit(Project project, String change, Date changeTime) {
        return audit(project, change, changeTime, AuditLevel.INFO);
    }

    public List<AuditRecord> getAuditRecords(Date startDate, Date endDate, Optional<Long> projectId, int start, int records) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && SitePermissionService.userHasPermission(currentUser.get(), SitePermission.VIEW_AUDIT)) {
            if (projectId.isPresent()) {
                return auditRecordDao.findByDateRangeForProject(startDate, endDate, projectId.get(), start, records);
            } else {
                return auditRecordDao.findByDateRange(startDate, endDate, start, records);
            }
        }

        if (!currentUser.isPresent()) {
            LOG.error("Anonymous may not view audit records.");
        } else {
            LOG.error("User " + currentUser.get().getId() + " does not have the permission " + SitePermission.VIEW_AUDIT + " in getAuditRecords()");
        }

        throw new NoPermissionException("You do not have permission to view audit records");
    }

    private class AuditJob implements Runnable {
        private String change;
        private Date changeTime;
        private Optional<User> user;
        private AuditLevel auditLevel;
        private EntityManagerFactory entityManagerFactory;
        private Optional<Project> project;

        public AuditJob(String change, Date changeTime, Optional<User> user, Optional<Project> project, AuditLevel auditLevel, EntityManagerFactory entityManagerFactory) {
            this.change = change;
            this.changeTime = changeTime;
            this.user = user;
            this.project = project;
            this.auditLevel = auditLevel;
            this.entityManagerFactory = entityManagerFactory;
        }

        @Override
        public void run() {
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            try {
                AuditRecord auditRecord = new AuditRecord(change, changeTime, user.orElse(null), auditLevel, project.orElse(null));
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.persist(auditRecord);
                transaction.commit();

                // Clean up
                Date oldestRecord = new Date(System.currentTimeMillis() - (AUDIT_RETENTION_DAYS * 24L * 60L * 60L * 1000L));

                transaction.begin();
                Query query = entityManager.createQuery(
                        "delete from AuditRecord audit where audit.changeTime < :oldest");
                query.setParameter("oldest", oldestRecord.getTime());
                int rows = query.executeUpdate();
                if (rows > 0) {
                    LOG.info("Deleted " + rows + " old audit records");
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } catch (Exception e) {
                LOG.error("Failed to log audit record", e);
            } finally {
                entityManager.close();
            }
        }
    }
}
