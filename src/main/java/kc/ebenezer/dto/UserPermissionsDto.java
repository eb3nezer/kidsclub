package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPermissionsDto {
    private UserDto user;
    private ProjectDto project;
    private List<PermissionRecordDto> userSitePermissions;
    private List<PermissionRecordDto> userProjectPermissions;

    public UserPermissionsDto() {
    }

    public UserPermissionsDto(UserDto user, ProjectDto project, List<PermissionRecordDto> userSitePermissions, List<PermissionRecordDto> userProjectPermissions) {
        this.user = user;
        this.project = project;
        this.userSitePermissions = userSitePermissions;
        this.userProjectPermissions = userProjectPermissions;
    }

    public ProjectDto getProject() {
        return project;
    }

    public UserDto getUser() {
        return user;
    }

    public List<PermissionRecordDto> getUserSitePermissions() {
        return userSitePermissions;
    }

    public List<PermissionRecordDto> getUserProjectPermissions() {
        return userProjectPermissions;
    }
}
