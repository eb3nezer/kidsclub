package kc.ebenezer.dao;

import kc.ebenezer.model.UserSitePermission;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSitePermissionDao extends BaseDaoImpl<UserSitePermission> {
    @Override
    public Optional<UserSitePermission> findById(Long id) {
        return Optional.empty();
    }
}
