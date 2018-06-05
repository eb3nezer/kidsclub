package kc.ebenezer.dao;

import kc.ebenezer.model.Student;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class StudentDao extends BaseDaoImpl<Student> {
    @Override
    public Optional<Student> findById(Long id) {
        Student student = null;
        try {
            student = getEntityManager().find(Student.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(student);
    }

    public List<Student> studentsForProject(long projectId) {
        TypedQuery<Student> query = getEntityManager().createQuery("select student from Student student " +
                "where student.project.id = :projectId", Student.class);
        query.setParameter("projectId", projectId);
        List<Student> result = query.getResultList();
        result.sort(new Student.StudentComparator());
        return result;
    }

    public List<Student> findForProjectMatchingName(long projectId, String name, Integer limit) {
        TypedQuery<Student> query = getEntityManager().createQuery(
                "select student from Student student where student.project.id = :projectId and lower(student.name) like '%' || :name || '%'", Student.class);
        query.setParameter("name", name.toLowerCase());
        query.setParameter("projectId", projectId);
        query.setMaxResults(limit);
        List<Student> result = query.getResultList();
        result.sort(new Student.StudentComparator());
        return result;
    }

    public Optional<Student> findForProjectAndExactName(long projectId, String name) {
        TypedQuery<Student> query = getEntityManager().createQuery(
                "select student from Student student where student.project.id = :projectId and student.name = :name", Student.class);
        query.setParameter("name", name);
        query.setParameter("projectId", projectId);
        List<Student> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }
}
