package ebenezer.rest;

import ebenezer.dto.ProjectDto;
import ebenezer.dto.UserDetailsDto;
import ebenezer.dto.mapper.ProjectMapper;
import ebenezer.dto.mapper.UserMapper;
import ebenezer.model.Project;
import ebenezer.model.User;
import ebenezer.service.ProjectService;
import ebenezer.service.StatsService;
import ebenezer.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.*;

@Component
@Path("/projects")
public class ProjectResource {
    @Inject
    private ProjectService projectService;
    @Inject
    private ProjectMapper projectMapper;
    @Inject
    private UserService userService;
    @Inject
    private StatsService statsService;

    @GET
    @Produces("application/json")
    public Response getProjectsForCurrentUser() {
        Optional<User> user = userService.getCurrentUser();
        if (user.isPresent()) {
            List<Project> projects = projectService.getProjectsForUser(user.get().getId());
            List<ProjectDto> projectDtos = projectMapper.toDto(projects);
            logStats("rest.projects.get", null);
            return Response.status(Response.Status.OK).entity(projectDtos).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response createProject(ProjectDto projectDto) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isPresent()) {
            Optional<Project> created = projectService.createProject(projectMapper.toModel(projectDto));
            if (created.isPresent()) {
                logStats("rest.project.create", created.get().getId().toString());
                return Response.status(Response.Status.OK).entity(projectMapper.toDto(created.get())).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces("application/json")
    @Path("/{id}")
    public Response getProject(@PathParam("id") String id) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isPresent()) {
            Optional<Project> project = projectService.getProjectById(user.get(), Long.valueOf(id));
            if (project.isPresent()) {
                logStats("rest.project.get", id);
                return Response.status(Response.Status.OK).entity(projectMapper.toDto(project.get())).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private void logStats(String key, String projectId) {
        Map<String, String> metadata = new HashMap<>();
        if (projectId != null) {
            metadata.put("PROJECT", projectId);
        }
        statsService.logStats(key, metadata);
    }
}
