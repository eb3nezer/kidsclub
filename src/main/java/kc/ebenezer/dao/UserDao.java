package kc.ebenezer.dao;

import kc.ebenezer.model.Project;
import kc.ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

@Component
public class UserDao extends BaseDaoImpl<User> {
    @Override
    public Optional<User> findById(Long id) {
        User user = null;
        try {
            TypedQuery<User> query = getEntityManager().createQuery(
                "select user from User user " +
                    "left join fetch user.userSitePermissions " +
                    "left join fetch user.projects " +
                    "where user.id = :id", User.class);
            query.setParameter("id", id);
            user = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(user);
    }

    public Optional<User> findByEmail(String email) {
        User user = null;
        try {
            TypedQuery<User> query = getEntityManager().createQuery(
                "select user from User user " +
                    "left join fetch user.userSitePermissions " +
                    "left join fetch user.projects " +
                    "where lower(user.email) = :email", User.class);
            query.setParameter("email", email.toLowerCase());
            user = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(user);
    }

    public List<User> findForProject(long projectId) {
        TypedQuery<User> query = getEntityManager().createQuery("select user from User user where user.projects.id = :projectId", User.class);
        query.setParameter("projectId", projectId);
        List<User> result = query.getResultList();
        result.sort(new User.UserComparator());
        return result;
    }

    public List<User> findMatchingName(String name, Integer limit) {
        TypedQuery<User> query = getEntityManager().createQuery(
                "select user from User user where lower(user.name) like '%' || :name || '%'", User.class);
        query.setParameter("name", name.toLowerCase());
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<User> findForProjectMatchingName(long projectId, String name, Integer limit) {
        TypedQuery<User> query = getEntityManager().createQuery(
                "select user from User user left join user.projects project where project.id = :projectId and lower(user.name) like '%' || :name || '%'", User.class);
        query.setParameter("name", name.toLowerCase());
        query.setParameter("projectId", projectId);
        query.setMaxResults(limit);
        List<User> result = query.getResultList();
            result.sort(new User.UserComparator());
        return result;
    }

    public List<User> listAll() {
        List<User> result = getEntityManager().createQuery("select user from User user", User.class).getResultList();
        result.sort(new User.UserComparator());
        return result;
    }
}
