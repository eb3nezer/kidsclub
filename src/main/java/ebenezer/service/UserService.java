package ebenezer.service;

import ebenezer.dao.UserDao;
import ebenezer.dto.PermissionRecordDto;
import ebenezer.dto.UserDetailsDto;
import ebenezer.dto.UserInvitationDto;
import ebenezer.dto.UserPermissionsDto;
import ebenezer.dto.mapper.UserDetailsMapper;
import ebenezer.model.Project;
import ebenezer.model.User;
import ebenezer.permissions.ProjectPermission;
import ebenezer.permissions.SitePermission;
import ebenezer.rest.NoPermissionException;
import ebenezer.rest.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;
import org.codehaus.jackson.map.introspect.NopAnnotationIntrospector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @Inject
    private UserDao userDao;
    @Inject
    private ProjectService projectService;
    @Inject
    private AuditService auditService;
    @Inject
    private MediaService mediaService;
    @Inject
    private UserDetailsMapper userDetailsMapper;
    @Inject
    private ProjectPermissionService projectPermissionService;
    @Inject
    private PermissionsService permissionsService;

    public UserService() {
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User createUser(User user) {
        auditService.audit("Created new user, id=" + user.getId() + " email=" + user.getEmail(),
                new Date());
        User created = userDao.create(user);
        userDao.flush();
        return created;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> userOptional = getUserById(Long.valueOf(s));
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("No user for id=" + s);
        }

        return userDetailsMapper.toDto(userOptional.get());
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsDto) {
            UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();
            return getUserById(userDetailsDto.getId());
        }

        return Optional.empty();
    }

    /**
     * Get all users in the system, if permitted.
     * @return A list of all users.
     */
    public List<User> getAllUsers(Optional<String> nameMatch) {
        Optional<User> currentUser = getCurrentUser();
        if (!currentUser.isPresent() || !SitePermissionService.userHasPermission(currentUser.get(), SitePermission.LIST_USERS)) {
            throw new NoPermissionException("You do not have permission to list users");
        }

        if (!nameMatch.isPresent()) {
            return userDao.listAll();
        } else {
            return userDao.findMatchingName(nameMatch.get(), 10);
        }
    }

    /**
     * Get all users in a project.
     * @param projectId The ID of the project.
     * @param nameMatch The name to search for.
     * @return A list of all users that contain nameMatch in their name.
     */
    public List<User> getUsersForProject(long projectId, Optional<String> nameMatch) {
        Optional<User> currentUser = getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot list users");
        }
        // Check if this user is on this project
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent() && !(SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN)
                || SitePermissionService.userHasPermission(currentUser.get(), SitePermission.LIST_USERS))) {
            throw new NoPermissionException("You do not have permission to list users for this project");
        }

        if (nameMatch.isPresent()) {
            return userDao.findForProjectMatchingName(projectId, nameMatch.get(), 10);
        } else {
            return userDao.findForProject(projectId);
        }
    }

    private boolean valueUpdated(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return false;
        }

        if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
            return true;
        }

        return !o1.equals(o2);
    }

    public Optional<User> updateUser(User valuesToUpdate) {
        Optional<User> currentUser = getCurrentUser();
        if (!currentUser.isPresent() || !currentUser.get().getId().equals(valuesToUpdate.getId())) {
            throw new NoPermissionException("You may not update a user other than yourself");
        }

        Optional<User> existingUser = getUserById(valuesToUpdate.getId());
        Assert.isTrue(existingUser.isPresent(), "Could not find user id=" + valuesToUpdate.getId());

        Date now = new Date();

        if (!existingUser.get().getName().equals(valuesToUpdate.getName())) {
            if (valuesToUpdate.getName() == null || valuesToUpdate.getName().isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            existingUser.get().setName(valuesToUpdate.getName());
            existingUser.get().setUpdated(now);
            auditService.audit("Set name to \"" + valuesToUpdate.getName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getGivenName(), valuesToUpdate.getGivenName())) {
            existingUser.get().setGivenName(valuesToUpdate.getGivenName());
            existingUser.get().setUpdated(now);
            auditService.audit("Set given name to \"" + valuesToUpdate.getGivenName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getFamilyName(), valuesToUpdate.getFamilyName())) {
            existingUser.get().setFamilyName(valuesToUpdate.getFamilyName());
            existingUser.get().setUpdated(now);
            auditService.audit("Set family name to \"" + valuesToUpdate.getFamilyName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getMobilePhone(), valuesToUpdate.getMobilePhone())) {
            existingUser.get().setMobilePhone(valuesToUpdate.getMobilePhone());
            existingUser.get().setUpdated(now);
            auditService.audit("Set mobile number to \"" + valuesToUpdate.getMobilePhone() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getHomePhone(), valuesToUpdate.getHomePhone())) {
            existingUser.get().setHomePhone(valuesToUpdate.getHomePhone());
            existingUser.get().setUpdated(now);
            auditService.audit("Set home number to \"" + valuesToUpdate.getHomePhone() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getAvatarUrl(), valuesToUpdate.getAvatarUrl())) {
            try {
                new URL(valuesToUpdate.getAvatarUrl());
            } catch (MalformedURLException e) {
                throw new ValidationException("Avatar URL does not look like a valid URL.");
            }
            existingUser.get().setAvatarUrl(valuesToUpdate.getAvatarUrl());
            existingUser.get().setUpdated(now);
            auditService.audit("Set avatar URL to \"" + valuesToUpdate.getAvatarUrl() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            if (existingUser.get().getMediaDescriptor() != null && !existingUser.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingUser.get().getMediaDescriptor());
                auditService.audit("Deleting media \"" + existingUser.get().getMediaDescriptor() +
                                "\" so it can be replaced with for user id=" + valuesToUpdate.getMediaDescriptor(),
                        now);
            }
            existingUser.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            existingUser.get().setUpdated(now);
            auditService.audit("Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        return existingUser;
    }

    public Optional<User> inviteNewUser(User invitingUser, UserInvitationDto userInvitationDto) {
        Optional<Project> project = projectService.getProjectById(invitingUser, userInvitationDto.getProjectId());
        if (!project.isPresent()) {
            return Optional.empty();
        }

        if (!(SitePermissionService.userHasPermission(invitingUser, SitePermission.INVITE_USERS) ||
                SitePermissionService.userHasPermission(invitingUser, SitePermission.SYSTEM_ADMIN))) {
            throw new NoPermissionException("You do not have permission to invite users");
        }

        // Check if this user is already a project member
        for (User existingUser : project.get().getUsers()) {
            if (existingUser.getEmail().equalsIgnoreCase(userInvitationDto.getEmail())) {
                return Optional.of(existingUser);
            }
        }

        Optional<User> userToInvite = getUserByEmail(userInvitationDto.getEmail());
        if (!userToInvite.isPresent()) {
            EmailValidator emailValidator = EmailValidator.getInstance();
            if (!emailValidator.isValid(userInvitationDto.getEmail())) {
                throw new ValidationException("The email address is not valid");
            }

            String name = userInvitationDto.getName() != null ? userInvitationDto.getName() : userInvitationDto.getEmail();
            User newUser = new User(null,
                    name,
                    null,
                    null,
                    userInvitationDto.getEmail(),
                    null,
                    null,
                    false,
                    true,
                    null,
                    null,
                    null,
                    null);
            userToInvite = Optional.of(userDao.create(newUser));
            userDao.flush();
            auditService.audit("Created new user for invite. New user id=" + userToInvite.get().getId() +
                            " email=\"" + userInvitationDto.getEmail() + "\"", new Date());
        }

        project.get().getUsers().add(userToInvite.get());
        auditService.audit("Added user id=" + userToInvite.get().getId() +
                " to project id=" + project.get().getId(), new Date());
        permissionsService.updateProjectPermission(
                invitingUser, userToInvite.get().getId(), project.get().getId(), ProjectPermission.LIST_USERS,true);
        permissionsService.updateProjectPermission(
                invitingUser, userToInvite.get().getId(), project.get().getId(), ProjectPermission.VIEW_STUDENTS,true);
        permissionsService.updateProjectPermission(
                invitingUser, userToInvite.get().getId(), project.get().getId(), ProjectPermission.EDIT_ALBUMS, true);

        return userToInvite;
    }

    public boolean unInviteUser(Long userId, UserInvitationDto userInvitationDto) {
        Optional<User> currentUser = getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous may not uninvite users");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), userInvitationDto.getProjectId());
        if (!project.isPresent()) {
            throw new NoPermissionException("You are not a member of this project");
        }
        if (!(SitePermissionService.userHasPermission(currentUser.get(), SitePermission.INVITE_USERS) ||
                SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN))) {
            throw new NoPermissionException("You do not have permission to invite/uninvite users");
        }

        // Check if this user is already a project member
        for (User existingUser : project.get().getUsers()) {
            if (existingUser.getId().equals(userId)) {
                // Revoke permissions
                UserPermissionsDto userPermissions = permissionsService.getUserPermissions(userId, project.get().getId());
                for (PermissionRecordDto projectPermission : userPermissions.getUserProjectPermissions()) {
                    if (projectPermission.isGranted()) {
                        permissionsService.updateProjectPermission(
                                currentUser.get(),
                                userId,
                                project.get().getId(),
                                ProjectPermission.valueOf(projectPermission.getKey()),
                                false);
                    }
                }
                project.get().getUsers().remove(existingUser);
                existingUser.getProjects().remove(project.get());
                userDao.flush();
                auditService.audit("Removed user id=" + existingUser.getId() +
                        " from project id=" + project.get().getId(), new Date());
                List<Project> projects = projectService.getProjectsForUser(userId);
                if (projects.isEmpty()) {
                    for (PermissionRecordDto sitePermission : userPermissions.getUserSitePermissions()) {
                        if (sitePermission.isGranted()) {
                            permissionsService.updateSitePermission(currentUser.get(),
                                    userId,
                                    SitePermission.valueOf(sitePermission.getKey()),
                                    false,
                                    true);
                        }
                    }
                    auditService.audit("Deleting user id=" + userId + " as this user is not a member of any project", new Date());
                    userDao.delete(existingUser);
                }
                return true;
            }
        }

        return false;
    }
}