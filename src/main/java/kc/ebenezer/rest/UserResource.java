package kc.ebenezer.rest;

import kc.ebenezer.Application;
import kc.ebenezer.dto.PermissionRecordDto;
import kc.ebenezer.dto.UserDto;
import kc.ebenezer.dto.UserPermissionsDto;
import kc.ebenezer.dto.mapper.UserMapper;
import kc.ebenezer.model.Media;
import kc.ebenezer.model.User;
import kc.ebenezer.service.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@Path("/users")
public class UserResource {
    @Inject
    private UserService userService;
    @Inject
    private UserMapper userMapper;
    @Inject
    private MediaService mediaService;
    @Inject
    private StatsService statsService;
    @Inject
    private PermissionsService permissionsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers(@QueryParam("projectId") String project, @QueryParam("name") String name) {
            List<User> users;
            if (project == null) {
                users = userService.getAllUsers(Optional.ofNullable(name));
                logStats("rest.users.get", null);
            } else {
                users = userService.getUsersForProject(Long.valueOf(project), Optional.ofNullable(name));
                logStats("rest.users.get", project);
            }

            List<UserDto> userDtos = userMapper.toDto(users);
            return Response.status(Response.Status.OK).entity(userDtos).build();
    }

    @Path("/me")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser() {
        Optional<User> user = userService.getCurrentUser();

        if (user.isPresent()) {
            // Get a new copy from the DB
            user = userService.getUserById(user.get().getId());
            if (user.isPresent()) {
                logStats("rest.user.current.get", null);
                return Response.status(Response.Status.OK).entity(userMapper.toDto(user.get())).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCurrentUserForNewUser(UserDto updatedUser) {
        User updateRequest = userMapper.toModel(updatedUser);
        Optional<User> updated = userService.updateUser(updateRequest);
        if (updated.isPresent()) {
            logStats("rest.user.new.update", null);
            return Response.status(Response.Status.OK).entity(userMapper.toDto(updated.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCurrentUserForExistingUser(
            @Context HttpServletRequest request,
            @FormDataParam("id") String userId,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("name") String name,
            @FormDataParam("givenName") String givenName,
            @FormDataParam("familyName") String familyName,
            @FormDataParam("avatarUrl") String avatarUrl,
            @FormDataParam("mobilePhone") String mobilePhone,
            @FormDataParam("homePhone") String homePhone,
            @FormDataParam("mediaDescriptor") String mediaDescriptor
    ) throws IOException {
        // Check for file upload
        if (payload != null && payloadDetail != null && !payloadDetail.getFileName().isEmpty()) {
            Optional<Media> media = mediaService.storeData(payload, request.getContentLength(), payloadDetail.getFileName(), true, name);
            if (media.isPresent()) {
                mediaDescriptor = media.get().getDescriptor();
            }
        }
        if (mobilePhone != null && (mobilePhone.equals("null") || mobilePhone.trim().isEmpty())) {
            mobilePhone = null;
        }
        if (homePhone != null && (homePhone.equals("null") || homePhone.trim().isEmpty())) {
            homePhone = null;
        }
        if (name != null && (name.equals("null") || name.trim().isEmpty())) {
            name = null;
        }
        if (givenName != null && (givenName.equals("null") || givenName.trim().isEmpty())) {
            givenName = null;
        }
        if (familyName != null && (familyName.equals("null") || familyName.trim().isEmpty())) {
            familyName = null;
        }
        if (avatarUrl != null && (avatarUrl.equals("null") || avatarUrl.trim().isEmpty())) {
            avatarUrl = null;
        }
        if (mediaDescriptor != null && (mediaDescriptor.equals("null") || mediaDescriptor.trim().isEmpty())) {
            mediaDescriptor = null;
        }
        UserDto updatedUser = new UserDto(
                Long.valueOf(userId),
                name,
                givenName,
                familyName,
                null,
                homePhone,
                mobilePhone,
                null,
                null,
                avatarUrl,
                mediaDescriptor,
                null,
                null,
                Collections.EMPTY_LIST);

        User updateRequest = userMapper.toModel(updatedUser);
        Optional<User> updated = userService.updateUser(updateRequest);
        if (updated.isPresent()) {
            logStats("rest.user.update", null);
            return Response.status(Response.Status.OK).entity(userMapper.toDto(updated.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUser(@PathParam("id") String id) {
        Optional<User> user = userService.getUserById(Long.valueOf(id));
        if (user.isPresent()) {
            logStats("rest.user.get", null);
            return Response.status(Response.Status.OK).entity(userMapper.toDto(user.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/me/permissions")
    public Response getMyPermissions(@QueryParam("projectId") String projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            logStats("rest.user.get.permissions", projectId);
            UserPermissionsDto userPermissions;
            if (projectId != null) {
                userPermissions = permissionsService.getUserPermissions(currentUser.get(), Long.valueOf(projectId));
            } else {
                userPermissions = permissionsService.getUserPermissions(currentUser.get());
            }

            return Response.status(Response.Status.OK).entity(userPermissions).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/permissions")
    public Response getUserPermissions(@QueryParam("projectId") String projectId, @PathParam("id") String id) {
        Optional<User> currentUser = userService.getUserById(Long.valueOf(id));
        if (currentUser.isPresent()) {
            logStats("rest.user.get.permissions", projectId);
            UserPermissionsDto userPermissions;
            if (projectId != null) {
                userPermissions = permissionsService.getUserPermissions(Long.valueOf(id), Long.valueOf(projectId));
            } else {
                userPermissions = permissionsService.getUserPermissions(Long.valueOf(id));
            }

            return Response.status(Response.Status.OK).entity(userPermissions).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/permissions")
    public Response updateUserPermissions(@PathParam("id") String id, UserPermissionsDto userPermissionsDto) {
        Optional<User> currentUser = userService.getUserById(Long.valueOf(id));
        if (currentUser.isPresent()) {
            UserPermissionsDto result = permissionsService.setUserPermissionsDto(Long.valueOf(id), userPermissionsDto);
            String projectId = null;
            if (userPermissionsDto.getProject() != null) {
                projectId = userPermissionsDto.getProject().getId().toString();
            }
            logStats("rest.user.set.permissions", projectId);

            return Response.status(Response.Status.OK).entity(result).build();
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
