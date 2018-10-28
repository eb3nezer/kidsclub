package kc.ebenezer.service;

import kc.ebenezer.dao.UserProjectPermissionDao;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserProjectPermission;
import kc.ebenezer.model.UserProjectPermissionContext;
import kc.ebenezer.permissions.ProjectPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectPermissionService {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectPermissionService.class);

    @Inject
    private UserProjectPermissionDao userProjectPermissionDao;
    @Inject
    private UserService userService;

    public boolean userHasPermission(User user, Project project, ProjectPermission permission) {
        List<ProjectPermission> permissions;
        UserProjectPermissionContext userProjectPermissionContext = userService.getUserProjectPermissionContext();
        if (userProjectPermissionContext != null && userProjectPermissionContext.getUser().equals(user)) {
            // cached permissions
            permissions = userProjectPermissionContext.getUserProjectPermissions(project)
                .stream()
                .map(UserProjectPermission::getPermissionKey)
                .collect(Collectors.toList());
        } else {
            // fall back to looking it up
            LOG.info("Project permissions cache miss for user " + user.getId());
            permissions= userProjectPermissionDao.getPermissionsForUserAndProject(user.getId(), project.getId())
                .stream()
                .map(UserProjectPermission::getPermissionKey)
                .collect(Collectors.toList());
        }
        return permissions.contains(permission);
    }

    public static boolean userIsProjectMember(User user, Project project) {
        boolean result = false;
        for (User thisUser : project.getUsers()) {
            if (thisUser.getId().equals(user.getId())) {
                result = true;
            }
        }

        return result;
    }
}
