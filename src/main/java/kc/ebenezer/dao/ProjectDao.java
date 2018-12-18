package kc.ebenezer.dao;

import kc.ebenezer.model.Project;
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
            TypedQuery<Project> query = getEntityManager().createQuery(
                "select project from Project project " +
                    "join fetch project.users user " +
                    "left join fetch user.userSitePermissions " +
                    "where project.id = :id", Project.class);
            query.setParameter("id", id);
            project = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(project);
    }

    public List<Project> getProjectsForUser(Long userId, boolean includeDisabled) {
        String hql = "select project from Project project " +
            "join fetch project.users user " +
            "where user.id = :userId";
        if (!includeDisabled) {
            hql += " and (project.disabled is null or project.disabled = false)";
        }
        TypedQuery<Project> query = getEntityManager().createQuery(hql, Project.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public Optional<Project> findByIdWithoutPermissions(Long id) {
        Project project = null;
        try {
            TypedQuery<Project> query = getEntityManager().createQuery(
                "select project from Project project " +
                    "join fetch project.users " +
                    "where project.id = :id", Project.class);
            query.setParameter("id", id);
            project = query.getSingleResult();
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
