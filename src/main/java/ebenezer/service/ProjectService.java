package ebenezer.service;

import ebenezer.dao.ProjectDao;
import ebenezer.dao.UserDao;
import ebenezer.dto.mapper.ProjectMapper;
import ebenezer.dto.mapper.UserDetailsMapper;
import ebenezer.model.Project;
import ebenezer.model.User;
import ebenezer.permissions.ProjectPermission;
import ebenezer.permissions.SitePermission;
import ebenezer.rest.NoPermissionException;
import ebenezer.rest.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService  {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    private UserService userService;
    @Inject
    private ProjectDao projectDao;
    @Inject
    private AuditService auditService;
    @Inject
    private PermissionsService permissionsService;

    public ProjectService() {
    }

    public Optional<Project> getProjectById(User user, Long id) {
        if (id == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        Optional<Project> project = projectDao.findById(id);
        if (project.isPresent()) {
            if (ProjectPermissionService.userIsProjectMember(user, project.get())) {
                return project;
            }
        }

        return Optional.empty();
    }

    public List<Project> getProjectsForUser(Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return new ArrayList<>(user.get().getProjects());
        }

        return Collections.emptyList();
    }

    public Optional<Project> createProject(Project project) {
        Optional<User> user = userService.getCurrentUser();
        if (!user.isPresent()) {
            throw new NoPermissionException("Anonymous may not create projects");
        }
        if (!SitePermissionService.userHasPermission(user.get(), SitePermission.CREATE_PROJECT)) {
            throw new NoPermissionException("You do not have permission to create projects");
        }

        if (project.getName() == null || project.getName().isEmpty()) {
            throw new ValidationException("The project name must not be blank");
        }

        if (projectDao.findByName(project.getName()).isPresent()) {
                throw new ValidationException("There is already a project with this name");
        }

        project.getUsers().add(user.get());
        Project created = projectDao.create(project);
        if (created == null) {
            LOG.error("Failed to create new project");
            return Optional.empty();
        }
        projectDao.flush();

        auditService.audit("Created new project. name=\"" + project.getName() +
                "\" id=" + project.getId(),
                new Date(created.getCreated()));
        // Grant all project permissions
        for (ProjectPermission projectPermission : ProjectPermission.values()) {
            permissionsService.updateProjectPermission(user.get(), user.get().getId(), project.getId(), projectPermission, true);
        }

        return Optional.of(project);
    }
}
