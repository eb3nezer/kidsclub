package ebenezer.dao;

import ebenezer.model.StudentTeam;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class StudentTeamDao extends BaseDaoImpl<StudentTeam> {
    @Override
    public Optional<StudentTeam> findById(Long id) {
        StudentTeam studentTeam = null;
        try {
            studentTeam = getEntityManager().find(StudentTeam.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(studentTeam);
    }

    public List<StudentTeam> getStudentTeamsForProject(long projectId) {
        TypedQuery<StudentTeam> query = getEntityManager().createQuery("select studentTeam from StudentTeam studentTeam " +
                "where studentTeam.project.id = :projectId", StudentTeam.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }

    public Optional<StudentTeam> findByNameForProject(Long projectId, String teamName) {
        StudentTeam studentTeam = null;

        TypedQuery<StudentTeam> query = getEntityManager().createQuery("select studentTeam from StudentTeam studentTeam " +
                "where studentTeam.project.id = :projectId " +
                "and studentTeam.name = :teamName", StudentTeam.class);
        query.setParameter("projectId", projectId);
        query.setParameter("teamName", teamName);
        try {
            studentTeam = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(studentTeam);
    }
}
