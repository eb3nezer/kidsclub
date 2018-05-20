package ebenezer.dao;

import ebenezer.model.Project;
import ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class ProjectDao extends BaseDaoImpl<Project> {
    @Override
    public Optional<Project> findById(Long id) {
        Project project = null;
        try {
            project = getEntityManager().find(Project.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(project);
    }

    public Optional<Project> findByName(String name) {
        Project project = null;
        try {
            TypedQuery<Project> query = getEntityManager().createQuery(
                    "select project from Project project where lower(project.name) = :name", Project.class);
            query.setParameter("name", name.toLowerCase());
            project = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(project);
    }
}
