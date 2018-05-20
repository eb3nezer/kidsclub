package ebenezer.service;

import ebenezer.dao.UserProjectPermissionDao;
import ebenezer.model.Project;
import ebenezer.model.User;
import ebenezer.model.UserProjectPermission;
import ebenezer.permissions.ProjectPermission;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectPermissionService {
    @Inject
    private UserProjectPermissionDao userProjectPermissionDao;

    public boolean userHasPermission(User user, Project project, ProjectPermission permission) {
        List<ProjectPermission> permissions = userProjectPermissionDao.getPermissionsForUserAndProject(user.getId(), project.getId())
                .stream()
                .map(upm -> upm.getPermissionKey())
                .collect(Collectors.toList());
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
