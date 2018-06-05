package kc.ebenezer.dao;

import kc.ebenezer.model.UserProjectPermission;
import kc.ebenezer.model.UserSitePermission;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class UserProjectPermissionDao extends BaseDaoImpl<UserProjectPermission> {
    @Override
    public Optional<UserProjectPermission> findById(Long id) {
        return Optional.empty();
    }

    public List<UserProjectPermission> getPermissionsForUserAndProject(Long userId, Long projectId) {
        TypedQuery<UserProjectPermission> query = getEntityManager().createQuery(
                "select userProjectPermission from UserProjectPermission userProjectPermission " +
                        "where userProjectPermission.key.project.id = :projectId " +
                        "and userProjectPermission.key.user.id = :userId", UserProjectPermission.class);
        query.setParameter("projectId", projectId);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
