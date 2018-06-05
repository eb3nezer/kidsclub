package ebenezer.dao;

import ebenezer.model.AttendanceRecord;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AttendanceRecordDao extends BaseDaoImpl<AttendanceRecord> {
    @Override
    public Optional<AttendanceRecord> findById(Long id) {
        AttendanceRecord attendanceRecord = null;
        try {
            attendanceRecord = getEntityManager().find(AttendanceRecord.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(attendanceRecord);
    }

    public List<AttendanceRecord> getAttendanceForStudentAndDateRange(Long studentId, Date earliest, Date latest, Optional<Integer> limit) {
        StringBuilder queryText = new StringBuilder();
        queryText.append("select attend from AttendanceRecord attend " +
                "where attend.student.id = :studentId " +
                "and attend.recordTime >= :earliest " +
                "and attend.recordTime <= :latest " +
                "order by attend.recordTime");
        if (limit.isPresent()) {
            queryText.append(" desc");
        }
        TypedQuery<AttendanceRecord> query = getEntityManager().createQuery(queryText.toString(), AttendanceRecord.class);
        query.setParameter("studentId", studentId);
        query.setParameter("earliest", earliest.getTime());
        query.setParameter("latest", latest.getTime());
        if (limit.isPresent()) {
            query.setMaxResults(limit.get());
        }
        return query.getResultList();
    }

    public List<AttendanceRecord> getAttendanceForDateRange(Long projectId, Date earliest, Date latest, Integer limit) {
        TypedQuery<AttendanceRecord> query = getEntityManager().createQuery("select attend from AttendanceRecord attend " +
                "left join attend.student student " +
                "left join student.project project " +
                "where project.id = :projectId " +
                "and attend.recordTime >= :earliest " +
                "and attend.recordTime <= :latest " +
                "order by attend.recordTime desc", AttendanceRecord.class);
        query.setParameter("projectId", projectId);
        query.setParameter("earliest", earliest.getTime());
        query.setParameter("latest", latest.getTime());
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<AttendanceRecord> getAttendanceForStudent(Long studentId) {
        TypedQuery<AttendanceRecord> query = getEntityManager().createQuery("select attend from AttendanceRecord attend " +
                "where attend.student.id = :studentId " +
                "order by attend.recordTime", AttendanceRecord.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }
}
