package kc.ebenezer.rest;

import kc.ebenezer.dto.ProjectDto;
import kc.ebenezer.dto.mapper.ProjectMapper;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.User;
import kc.ebenezer.service.ProjectService;
import kc.ebenezer.service.StatsService;
import kc.ebenezer.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/{id}")
    public Response updateProject(@PathParam("id") Long projectId, ProjectDto projectDto) {
        Optional<Project> updated = projectService.updateProject(projectId, projectMapper.toModel(projectDto));
        if (updated.isPresent()) {
            logStats("rest.project.updated", updated.get().getId().toString());
            return Response.status(Response.Status.OK).entity(projectMapper.toDto(updated.get())).build();
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
