package kc.ebenezer.service;

import kc.ebenezer.dao.ProjectDao;
import kc.ebenezer.dao.UserDao;
import kc.ebenezer.dto.mapper.ProjectMapper;
import kc.ebenezer.dto.mapper.UserDetailsMapper;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.ProjectProperty;
import kc.ebenezer.model.User;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
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

        auditService.audit(project, "Created new project. name=\"" + project.getName() +
                "\" id=" + project.getId(),
                new Date(created.getCreated()));
        // Grant all project permissions
        for (ProjectPermission projectPermission : ProjectPermission.values()) {
            permissionsService.updateProjectPermission(user.get(), user.get().getId(), project.getId(), projectPermission, true);
        }

        return Optional.of(project);
    }

    public Optional<Project> updateProject(Long projectId, Project projectToUpdate) {
        Optional<User> user = userService.getCurrentUser();
        if (!user.isPresent()) {
            throw new NoPermissionException("Anonymous may not update projects");
        }
        if (!SitePermissionService.userHasPermission(user.get(), SitePermission.CREATE_PROJECT)) {
            throw new NoPermissionException("You do not have permission to edit projects");
        }
        Optional<Project> existingProject = getProjectById(user.get(), projectId);
        if (!existingProject.isPresent()) {
            throw new ValidationException("Invalid project ID");
        }

        if (projectToUpdate.getName() == null || projectToUpdate.getName().isEmpty()) {
            throw new ValidationException("The project name must not be blank");
        }

        Date now = new Date();
        if (!existingProject.get().getName().equals(projectToUpdate.getName())) {
            if (projectDao.findByName(projectToUpdate.getName()).isPresent()) {
                throw new ValidationException("There is already a project with this name");
            }
            existingProject.get().setName(projectToUpdate.getName());
            auditService.audit(existingProject.get(), "Changed name for project id=" + projectId + " to \"" + projectToUpdate.getName() + "\"", now);
            existingProject.get().setUpdated(now);
        }

        for (ProjectProperty projectProperty : projectToUpdate.getProjectProperties()) {
            if (!existingProject.get().getProjectProperties().contains(projectProperty)) {
                ProjectProperty newProjectProperty = new ProjectProperty(existingProject.get(), projectProperty.getPropertyKey(), projectProperty.getPropertyValue());
                existingProject.get().getProjectProperties().add(newProjectProperty);
                auditService.audit(existingProject.get(), "Added new project property " + projectProperty.getPropertyKey() + "=" + projectProperty.getPropertyValue() +
                        " for project id=" + projectId, now);
                existingProject.get().setUpdated(now);
            } else {
                for (ProjectProperty existingProjectProperty : existingProject.get().getProjectProperties()) {
                    if (existingProjectProperty.getPropertyKey().equals(projectProperty.getPropertyKey())) {
                        if (!existingProjectProperty.getPropertyValue().equals(projectProperty.getPropertyValue())) {
                            existingProjectProperty.setPropertyValue(projectProperty.getPropertyValue());
                            auditService.audit(existingProject.get(), "Updating project property " + projectProperty.getPropertyKey() + "=" + projectProperty.getPropertyValue() +
                                    " for project id=" + projectId, now);
                            existingProject.get().setUpdated(now);
                        }
                    }
                }
            }
        }

        return existingProject;
    }

    public String getPropertyValue(Project project, String key) {
        String result = null;
        if (project.getProjectProperties() != null) {
            Optional<String> possibleResult = project.getProjectProperties()
                    .stream()
                    .filter(pp -> pp.getPropertyKey().equals(key))
                    .findFirst()
                    .map(ProjectProperty::getPropertyValue);
            if (possibleResult.isPresent()) {
                result = possibleResult.get();
            }
        }

        return result;
    }

    public boolean hasPropertyValue(Project project, String key) {
        return project.getProjectProperties()
            .stream()
            .filter(pp -> pp.getPropertyKey().equals(key))
            .findFirst()
            .isPresent();
    }

    public Boolean getPropertyValueAsBoolean(Project project, String key) {
        Boolean result = null;
        if (project.getProjectProperties() != null) {
            Optional<String> possibleResult = project.getProjectProperties()
                .stream()
                .filter(pp -> pp.getPropertyKey().equals(key))
                .findFirst()
                .map(ProjectProperty::getPropertyValue);
            if (possibleResult.isPresent()) {
                result = Boolean.valueOf(possibleResult.get());
            }
        }

        return result;
    }

    public Long getPropertyValueAsLong(Project project, String key) {
        Long result = null;
        if (project.getProjectProperties() != null) {
            Optional<String> possibleResult = project.getProjectProperties()
                .stream()
                .filter(pp -> pp.getPropertyKey().equals(key))
                .findFirst()
                .map(ProjectProperty::getPropertyValue);
            if (possibleResult.isPresent()) {
                result = Long.valueOf(possibleResult.get());
            }
        }

        return result;
    }


    public void setPropertyValue(Project project, String key, String value) {
        if (project.getProjectProperties() == null) {
            project.setProjectProperties(new HashSet<>());
        }
        Set<ProjectProperty> properties = project.getProjectProperties();
        boolean found = false;
        for (ProjectProperty projectProperty : properties) {
            if (projectProperty.getPropertyKey().equals(key)) {
                projectProperty.setPropertyValue(value);
                found = true;
                break;
            }
        }
        if (!found) {
            ProjectProperty projectProperty = new ProjectProperty(project, key, value);
            properties.add(projectProperty);
        }
    }
}
