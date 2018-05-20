package ebenezer.service;

import ebenezer.model.User;
import ebenezer.model.UserSitePermission;
import ebenezer.permissions.SitePermission;

import java.util.Optional;

public class SitePermissionService {
    public static boolean userHasPermission(User user, SitePermission permission) {
        Optional<SitePermission> found = user.getUserSitePermissions()
                .stream()
                .map(UserSitePermission::getPermissionKey)
                .filter(key -> key.equals(permission))
                .findAny();
        return found.isPresent();
    }
}
