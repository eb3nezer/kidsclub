package kc.ebenezer.service;

import kc.ebenezer.dao.UserProjectPermissionDao;
import kc.ebenezer.dao.UserSitePermissionDao;
import kc.ebenezer.dto.PermissionRecordDto;
import kc.ebenezer.dto.UserPermissionsDto;
import kc.ebenezer.dto.mapper.ProjectMapper;
import kc.ebenezer.dto.mapper.UserMapper;
import kc.ebenezer.model.*;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionsService {
    @Inject
    private UserService userService;
    @Inject
    private ProjectService projectService;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditService auditService;
    @Inject
    private UserSitePermissionDao userSitePermissionDao;
    @Inject
    private UserProjectPermissionDao userProjectPermissionDao;
    @Inject
    private ProjectPermissionService projectPermissionService;

    private List<PermissionRecordDto> getSitePermissionRecords(User user) {
        List<SitePermission> sitePermissions = user.getUserSitePermissions().stream()
                .map(UserSitePermission::getPermissionKey)
                .collect(Collectors.toList());
        List<PermissionRecordDto> grantedSitePermissions = Arrays.stream(SitePermission.values())
                .map(sp -> {
                    if (sitePermissions.contains(sp)) {
                        return new PermissionRecordDto(sp.toString(), sp.getDescription(), true);
                    } else {
                        return new PermissionRecordDto(sp.toString(), sp.getDescription(), false);
                    }
                })
                .collect(Collectors.toList());

        return grantedSitePermissions;
    }

    private List<PermissionRecordDto> getProjectPermissionRecords(Long userId, Long projectId) {
        List<UserProjectPermission> userProjectPermissions = userProjectPermissionDao.getPermissionsForUserAndProject(userId, projectId);

        List<ProjectPermission> projectPermissions = userProjectPermissions.stream()
                .map(UserProjectPermission::getPermissionKey)
                .collect(Collectors.toList());
        List<PermissionRecordDto> grantedSitePermissions = Arrays.stream(ProjectPermission.values())
                .map(sp -> {
                    if (projectPermissions.contains(sp)) {
                        return new PermissionRecordDto(sp.toString(), sp.getDescription(), true);
                    } else {
                        return new PermissionRecordDto(sp.toString(), sp.getDescription(), false);
                    }
                })
                .collect(Collectors.toList());

        return grantedSitePermissions;
    }

    public UserPermissionsDto getUserPermissions(User user) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot get user permissions");
        }

        List<PermissionRecordDto> grantedSitePermissions = getSitePermissionRecords(user);

        return new UserPermissionsDto(userMapper.toDto(user), null, grantedSitePermissions, null);
    }

    public UserPermissionsDto getUserPermissions(Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            throw new ValidationException("User does not exist");
        }

        return getUserPermissions(user.get());
    }

    public UserPermissionsDto getUserPermissions(User user, Long projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot get user permissions");
        }

        List<PermissionRecordDto> grantedProjectPermissions;
        Optional<Project> project = projectService.getProjectById(user, projectId);
        if (!project.isPresent()) {
            grantedProjectPermissions = new ArrayList<>();
        } else {
            grantedProjectPermissions = getProjectPermissionRecords(user.getId(), projectId);
        }

        List<PermissionRecordDto> grantedSitePermissions = getSitePermissionRecords(user);
        return new UserPermissionsDto(
            userMapper.toDto(user),
            projectMapper.toDtoNoUsers(project.orElse(null)),
            grantedSitePermissions,
            grantedProjectPermissions);
    }

    public UserPermissionsDto getUserPermissions(Long userId, Long projectId) {
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            throw new ValidationException("User does not exist");
        }
        return getUserPermissions(user.get(), projectId);
    }

    public UserPermissionsDto setUserPermissionsDto(Long targetUserId, UserPermissionsDto newPermissions) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot get user permissions");
        }

        if (newPermissions.getUserSitePermissions() != null) {
            for (PermissionRecordDto record : newPermissions.getUserSitePermissions()) {
                updateSitePermission(
                        currentUser.get(),
                        targetUserId,
                        SitePermission.valueOf(record.getKey()),
                        record.getGranted(), false);
            }
        }
        if (newPermissions.getProject() != null  && newPermissions.getProject().getId() != null &&
                newPermissions.getUserProjectPermissions() != null) {
            for (PermissionRecordDto record : newPermissions.getUserProjectPermissions()) {
                updateProjectPermission(
                        currentUser.get(),
                        targetUserId,
                        newPermissions.getProject().getId(),
                        ProjectPermission.valueOf(record.getKey()),
                        record.getGranted());
            }
        }
        return newPermissions;
    }

    public void updateSitePermission(
            User currentUser,
            Long targetUserId,
            SitePermission sitePermission,
            boolean grant,
            boolean overridePermission) {
        Optional<User> user = userService.getUserById(targetUserId);
        if (!user.isPresent()) {
            throw new ValidationException("User does not exist");
        }

        if (overridePermission) {
            auditService.audit(null, "Overriding access to change user permissions for user id=" + currentUser.getId(),
                    new Date(), AuditLevel.WARN);
        } else if (!SitePermissionService.userHasPermission(currentUser, SitePermission.SYSTEM_ADMIN)) {
            throw new NoPermissionException("You do not have permission to update site permissions");
        }

        List<SitePermission> currentSitePermissions = user.get().getUserSitePermissions().stream()
                .map(UserSitePermission::getPermissionKey)
                .collect(Collectors.toList());

        UserSitePermission userSitePermission = new UserSitePermission(user.get(), sitePermission);
        if (grant) {
            if (!currentSitePermissions.contains(sitePermission)) {
                auditService.audit(null, "Grant site permission " + sitePermission + " to user " + targetUserId, new Date());
                userSitePermissionDao.create(userSitePermission);
                user.get().getUserSitePermissions().add(userSitePermission);
            }
        } else {
            if (currentSitePermissions.contains(sitePermission)) {
                auditService.audit(null, "Revoke site permission " + sitePermission + " from user " + targetUserId, new Date());
                Optional<UserSitePermission> permissionToRevoke = user.get().getUserSitePermissions()
                        .stream()
                        .filter(p -> p.equals(userSitePermission))
                        .findFirst();
                permissionToRevoke.ifPresent(userSitePermission1 -> userSitePermissionDao.delete(userSitePermission1));
                user.get().getUserSitePermissions().remove(userSitePermission);
            }
        }
    }

    public void updateProjectPermission(User currentUser, Long targetUserId, Long projectId, ProjectPermission projectPermission, boolean grant) {
        Optional<User> user = userService.getUserById(targetUserId);
        if (!user.isPresent()) {
            throw new ValidationException("User does not exist");
        }

        Optional<Project> project = projectService.getProjectById(user.get(), projectId);
        if (!project.isPresent()) {
            throw new NoPermissionException("User id=" + user.get().getId() + " is not a member of project id=" + projectId);
        }

        if (!(SitePermissionService.userHasPermission(currentUser, SitePermission.SYSTEM_ADMIN) ||
                projectPermissionService.userHasPermission(currentUser, project.get(), ProjectPermission.PROJECT_ADMIN))) {
            throw new NoPermissionException("You do not have permission to update project permissions");
        }

        List<UserProjectPermission> currentProjectPermissions =
                userProjectPermissionDao.getPermissionsForUserAndProject(user.get().getId(), projectId);

        UserProjectPermission userProjectPermission =
                new UserProjectPermission(user.get(), project.get(), projectPermission);
        if (grant) {
            if (!currentProjectPermissions.contains(userProjectPermission)) {
                auditService.audit(
                    project.get(),
                        "Grant project permission " + projectPermission + " to user id=" + targetUserId +
                        " in project id=" + projectId, new Date());
                userProjectPermissionDao.create(userProjectPermission);
                currentProjectPermissions.add(userProjectPermission);
            }
        } else {
            if (currentProjectPermissions.contains(userProjectPermission)) {
                auditService.audit(project.get(),
                    "Revoke project permission " + projectPermission + " from user id=" + targetUserId +
                        " in project id=" + projectId, new Date());
                UserProjectPermission permissionToRevoke = currentProjectPermissions.remove(
                        currentProjectPermissions.indexOf(userProjectPermission));
                userProjectPermissionDao.delete(permissionToRevoke);
            }
        }
    }

}
