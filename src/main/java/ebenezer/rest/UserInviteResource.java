package ebenezer.rest;

import ebenezer.dto.UserInvitationDto;
import ebenezer.dto.mapper.UserMapper;
import ebenezer.model.User;
import ebenezer.service.StatsService;
import ebenezer.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/invite")
public class UserInviteResource {
    @Inject
    private UserService userService;
    @Inject
    private UserMapper userMapper;
    @Inject
    private StatsService statsService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response inviteNewUser(UserInvitationDto userInvitation) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            Optional<User> invited = userService.inviteNewUser(currentUser.get(), userInvitation);
            if (invited.isPresent()) {
                logStats("rest.user.invite", userInvitation.getProjectId());
                return Response.status(Response.Status.OK).entity(userMapper.toDto(invited.get())).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("/{id}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unInviteNewUser(@PathParam("id") String userId, UserInvitationDto userInvitation) {
        boolean result = userService.unInviteUser(Long.valueOf(userId), userInvitation);
        if (result) {
            logStats("rest.user.uninvite", userInvitation.getProjectId());
            return Response.status(Response.Status.OK).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private void logStats(String key, Long projectId) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("PROJECT", String.valueOf(projectId));
        statsService.logStats(key, metadata);
    }
}
