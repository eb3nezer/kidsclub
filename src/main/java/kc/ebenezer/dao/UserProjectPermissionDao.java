package kc.ebenezer.dao;

import kc.ebenezer.model.User;
import kc.ebenezer.model.UserProjectPermission;
import kc.ebenezer.model.UserProjectPermissionContext;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.Collections;
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

    public UserProjectPermissionContext getUserProjectPermissionContext(@NotNull User user) {
        TypedQuery<UserProjectPermission> query = getEntityManager().createQuery(
            "select userProjectPermission from UserProjectPermission userProjectPermission " +
                "join fetch  userProjectPermission.key.project " +
                "join fetch  userProjectPermission.key.user " +
                "where userProjectPermission.key.user.id = :userId", UserProjectPermission.class);
        query.setParameter("userId", user.getId());
        List<UserProjectPermission> permissions = query.getResultList();
        return new UserProjectPermissionContext(user, Collections.unmodifiableList(permissions));
    }
}
