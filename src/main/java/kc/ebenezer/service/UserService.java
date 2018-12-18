package kc.ebenezer.service;

import kc.ebenezer.dao.ImageCollectionDao;
import kc.ebenezer.dao.UserDao;
import kc.ebenezer.dto.*;
import kc.ebenezer.dto.mapper.UserMapper;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.StudentTeam;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserProjectPermissionContext;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserDao userDao;
    @Inject
    private ProjectService projectService;
    @Inject
    private AuditService auditService;
    @Inject
    private MediaService mediaService;
    @Inject
    private UserMapper userMapper;
    @Inject
    private PermissionsService permissionsService;
    @Inject
    private StudentTeamService studentTeamService;
    @Inject
    private ImageScalingService imageScalingService;
    @Inject
    private ImageCollectionDao imageCollectionDao;

    public UserService() {
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User createUser(User user) {
        auditService.audit(null, "Created new user, id=" + user.getId() + " email=" + user.getEmail(),
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

        return userMapper.toDto(userOptional.get());
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDto) {
            UserDto userDetailsDto = (UserDto) authentication.getPrincipal();
            return Optional.ofNullable(userMapper.toModel(userDetailsDto));
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
            List<User> users = userDao.findForProject(projectId);
            for (User user : users) {
                imageScalingService.repairOrCreateImageCollection(user, user.getMediaDescriptor());
            }
            return users;
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

        if (valueUpdated(existingUser.get().getName(), valuesToUpdate.getName())) {
            if (valuesToUpdate.getName() == null || valuesToUpdate.getName().isEmpty()) {
                throw new ValidationException("Name cannot be empty");
            }
            existingUser.get().setName(valuesToUpdate.getName());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set name to \"" + valuesToUpdate.getName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getGivenName(), valuesToUpdate.getGivenName())) {
            existingUser.get().setGivenName(valuesToUpdate.getGivenName());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set given name to \"" + valuesToUpdate.getGivenName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getFamilyName(), valuesToUpdate.getFamilyName())) {
            existingUser.get().setFamilyName(valuesToUpdate.getFamilyName());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set family name to \"" + valuesToUpdate.getFamilyName() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getMobilePhone(), valuesToUpdate.getMobilePhone())) {
            existingUser.get().setMobilePhone(valuesToUpdate.getMobilePhone());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set mobile number to \"" + valuesToUpdate.getMobilePhone() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getHomePhone(), valuesToUpdate.getHomePhone())) {
            existingUser.get().setHomePhone(valuesToUpdate.getHomePhone());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set home number to \"" + valuesToUpdate.getHomePhone() + "\" for user id=" + valuesToUpdate.getId(),
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
            auditService.audit(null, "Set avatar URL to \"" + valuesToUpdate.getAvatarUrl() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        if (valueUpdated(existingUser.get().getMediaDescriptor(), valuesToUpdate.getMediaDescriptor())) {
            imageScalingService.deleteOldImageCollection(existingUser.get());
            if (existingUser.get().getMediaDescriptor() != null && !existingUser.get().getMediaDescriptor().isEmpty()) {
                mediaService.deleteData(existingUser.get().getMediaDescriptor());
                auditService.audit(null, "Deleting media \"" + existingUser.get().getMediaDescriptor() +
                                "\" so it can be replaced with for user id=" + valuesToUpdate.getMediaDescriptor(),
                        now);
            }
            existingUser.get().setMediaDescriptor(valuesToUpdate.getMediaDescriptor());
            imageScalingService.repairOrCreateImageCollection(existingUser.get(), valuesToUpdate.getMediaDescriptor());
            existingUser.get().setUpdated(now);
            auditService.audit(null, "Set media descriptor to \"" + valuesToUpdate.getMediaDescriptor() + "\" for user id=" + valuesToUpdate.getId(),
                    now);
        }

        return existingUser;
    }

    private void inviteUserPermissionChecks(Optional<User> currentUser, Optional<Project> project) {
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous may not invite users");
        }
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project");
        }

        if (!(SitePermissionService.userHasPermission(currentUser.get(), SitePermission.INVITE_USERS) ||
                SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN))) {
            throw new NoPermissionException("You do not have permission to invite users");
        }
    }

    private Optional<User> inviteOneUser(Project project, User currentUser, String email, String name) {
        // Check if this user is already a project member
        for (User existingUser : project.getUsers()) {
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(existingUser);
            }
        }

        Optional<User> userToInvite = getUserByEmail(email);
        if (!userToInvite.isPresent()) {
            EmailValidator emailValidator = EmailValidator.getInstance();
            if (!emailValidator.isValid(email)) {
                throw new ValidationException("The email address is not valid");
            }

            if (name == null) {
                name = email;
            }
            User newUser = new User(null,
                    name,
                    null,
                    null,
                    email,
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
            auditService.audit(null, "Created new user for invite. New user id=" + userToInvite.get().getId() +
                    " email=\"" + email + "\"", new Date());
        }

        project.getUsers().add(userToInvite.get());
        auditService.audit(project, "Added user id=" + userToInvite.get().getId() +
                " to project id=" + project.getId(), new Date());
        permissionsService.updateProjectPermission(
                currentUser, userToInvite.get().getId(), project.getId(), ProjectPermission.LIST_USERS,true);
        permissionsService.updateProjectPermission(
                currentUser, userToInvite.get().getId(), project.getId(), ProjectPermission.VIEW_STUDENTS,true);
        permissionsService.updateProjectPermission(
                currentUser, userToInvite.get().getId(), project.getId(), ProjectPermission.EDIT_ALBUMS, true);

        return userToInvite;
    }

    @Transactional(dontRollbackOn = {ValidationException.class, NoPermissionException.class})
    public List<User> bulkInviteNewUsers(BulkUserInvitationDto bulkUserInvitationDto) {
        Optional<User> currentUser = getCurrentUser();
        Optional<Project> project = Optional.empty();
        if (currentUser.isPresent()) {
            project = projectService.getProjectById(currentUser.get(), bulkUserInvitationDto.getProjectId());
        }
        inviteUserPermissionChecks(currentUser, project);

        List<User> invited = new ArrayList<>();
        String[] emailAddresses = bulkUserInvitationDto.getEmails().split("\n");
        for (String emailAddress : emailAddresses) {
            emailAddress = emailAddress.trim();
            Optional<User> added = Optional.empty();
            if (!emailAddress.isEmpty()) {
                try {
                    added = inviteOneUser(project.get(), currentUser.get(), emailAddress, null);
                } catch (ValidationException e) {
                    LOG.warn("Validation failed when bulk inviting email address=\"" + emailAddress + "\"", e.getMessage());
                } catch (NoPermissionException e) {
                    LOG.warn("Permission error when bulk inviting email address=\"" + emailAddress + "\"", e.getMessage());
                }
            }
                if (added.isPresent()) {
                invited.add(added.get());
            }
        }

        return invited;
    }

    public Optional<User> inviteNewUser(UserInvitationDto userInvitationDto) {
        Optional<User> currentUser = getCurrentUser();
        Optional<Project> project = projectService.getProjectById(currentUser.get(), userInvitationDto.getProjectId());
        inviteUserPermissionChecks(currentUser, project);

        Optional<User> result = inviteOneUser(project.get(), currentUser.get(), userInvitationDto.getEmail(), userInvitationDto.getName());
        return result;
    }

    public boolean unInviteUser(Long userId, Long projectId) {
        Optional<User> currentUser = getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous may not uninvite users");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
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
                // Check if this user is a team leader
                List<StudentTeam> teams = studentTeamService.getStudentTeams(project.get().getId(), true);
                for (StudentTeam team : teams) {
                    team.getLeaders().remove(existingUser);
                }

                // Remove user from project
                project.get().getUsers().remove(existingUser);
                // Remove project from user
                existingUser.getProjects().remove(project.get());
                userDao.flush();
                auditService.audit(project.get(), "Removed user id=" + existingUser.getId() +
                        " from project id=" + project.get().getId(), new Date());

                // Check if this user is a member of any other projects
                List<Project> projects = projectService.getProjectsForUser(userId);
                if (projects.isEmpty()) {
                    // This user is not a member of any project, so remove site permissions
                    for (PermissionRecordDto sitePermission : userPermissions.getUserSitePermissions()) {
                        if (sitePermission.isGranted()) {
                            permissionsService.updateSitePermission(currentUser.get(),
                                    userId,
                                    SitePermission.valueOf(sitePermission.getKey()),
                                    false,
                                    true);
                        }
                    }
                    // Now delete the user entirely
                    auditService.audit(project.get(), "Deleting user id=" + userId + " as this user is not a member of any project", new Date());
                    userDao.delete(existingUser);
                }
                return true;
            }
        }

        return false;
    }

    public void setUserProjectPermissionContext(ServletRequest request, UserProjectPermissionContext userProjectPermissionContext) {
        request.setAttribute(UserProjectPermissionContext.class.getName(), userProjectPermissionContext);
    }

    public UserProjectPermissionContext getUserProjectPermissionContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return (UserProjectPermissionContext) request.getAttribute(UserProjectPermissionContext.class.getName());
        }
        return null;
    }
}
