package ebenezer.dao;

import ebenezer.model.AuditRecord;
import ebenezer.model.StatsRecord;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class StatsRecordDao extends BaseDaoImpl<StatsRecord> {
    @Override
    public Optional<StatsRecord> findById(Long id) {
        StatsRecord statsRecord = null;
        try {
            statsRecord = getEntityManager().find(StatsRecord.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(statsRecord);
    }

    public List<StatsRecord> findByDateRange(Date startDate, Date endDate) {
        TypedQuery<StatsRecord> query = getEntityManager().createQuery(
                "select record from StatsRecord record " +
                        "where record.created >= :startDate " +
                        "and record.created <= :endDate " +
                        "order by record.created desc", StatsRecord.class);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        return query.getResultList();
    }
}
