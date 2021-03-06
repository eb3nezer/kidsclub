package kc.ebenezer.dao;

import kc.ebenezer.model.AuditRecord;
import kc.ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AuditRecordDao extends BaseDaoImpl<AuditRecord> {
    @Override
    public Optional<AuditRecord> findById(Long id) {
        AuditRecord auditRecord = null;
        try {
            auditRecord = getEntityManager().find(AuditRecord.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(auditRecord);
    }

    public List<AuditRecord> findByDateRange(Date startDate, Date endDate, int start, int records) {
        TypedQuery<AuditRecord> query = getEntityManager().createQuery(
                "select record from AuditRecord record " +
                        "left join record.user user " +
                        "where record.changeTime >= :startDate " +
                        "and record.changeTime <= :endDate " +
                        "order by record.changeTime desc", AuditRecord.class);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setFirstResult(start);
        query.setMaxResults(records);
        return query.getResultList();
    }

    public List<AuditRecord> findByDateRangeForProject(Date startDate, Date endDate, Long projectId, int start, int records) {
        TypedQuery<AuditRecord> query = getEntityManager().createQuery(
            "select record from AuditRecord record " +
                "left join record.user user " +
                "where record.changeTime >= :startDate " +
                "and record.changeTime <= :endDate " +
                "and (record.project.id = :projectId or record.project.id is null) " +
                "order by record.changeTime desc", AuditRecord.class);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameter("projectId", projectId);
        query.setFirstResult(start);
        query.setMaxResults(records);
        return query.getResultList();
    }
}
