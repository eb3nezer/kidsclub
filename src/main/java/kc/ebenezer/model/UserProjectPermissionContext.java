package kc.ebenezer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProjectPermissionContext {
    private User user;
    private Map<Long, List<UserProjectPermission>> userProjectPermissionsByProject;

    public UserProjectPermissionContext(User user, List<UserProjectPermission> userProjectPermissions) {
        this.user = user;
        userProjectPermissionsByProject = new HashMap<>();
        for (UserProjectPermission userProjectPermission : userProjectPermissions) {
            if (!userProjectPermissionsByProject.containsKey(userProjectPermission.getProject().getId())) {
                userProjectPermissionsByProject.put(userProjectPermission.getProject().getId(), new ArrayList<UserProjectPermission>());
            }
            userProjectPermissionsByProject.get(userProjectPermission.getProject().getId()).add(userProjectPermission);
        }
    }

    public User getUser() {
        return user;
    }

    public List<UserProjectPermission> getUserProjectPermissions(Project project) {
        return userProjectPermissionsByProject.get(project.getId());
    }
}
