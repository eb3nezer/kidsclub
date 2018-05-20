package ebenezer.rest;

import ebenezer.Application;
import ebenezer.dto.MediaDto;
import ebenezer.dto.mapper.UserMapper;
import ebenezer.model.Media;
import ebenezer.service.MediaService;
import ebenezer.service.StatsService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Component
@Path("/data")
@Transactional
public class DataResource {
    private static final long MEDIA_EXPIRY_TIME = 30L * 24L * 60L * 60L * 1000L;

    @Inject
    private MediaService mediaService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private StatsService statsService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
            @Context HttpServletRequest request,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail) throws IOException, ServletException {
        int fileSize = request.getContentLength();
        if (fileSize > Application.MAX_UPLOAD_SIZE) {
            throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
        }
        byte[] data = new byte[fileSize];
        int bytesRead = 0;
        int chunk = 1024;
        int thisRead = 0;
        while ((thisRead = payload.read(data, bytesRead, chunk)) != -1) {
            bytesRead += thisRead;
        }
        byte[] actualData = new byte[bytesRead];
        System.arraycopy(data, 0, actualData, 0, bytesRead);
        String contentType;
        String filename = payloadDetail.getFileName().toLowerCase();
        if (filename.endsWith(".jpg")) {
            contentType = "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            contentType = "image/gif";
        } else if (filename.endsWith(".png")) {
            contentType = "image/png";
        } else {
            throw new ValidationException("Unrecognised file type");
        }
        Optional<Media> result = mediaService.storeData(actualData, contentType, false, "Photo");
        if (result.isPresent()) {
            statsService.logStats("rest.media.create", Collections.emptyMap());

            MediaDto mediaDto = new MediaDto(
                    result.get().getDescriptor(),
                    userMapper.toDto(result.get().getOwner()),
                    result.get().getContentType(),
                    result.get().getDescription());
            return Response.status(Response.Status.OK).entity(mediaDto).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/download/{descriptor}")
    public Response listUsers(@PathParam("descriptor") String descriptor) {
        Optional<Media> media = mediaService.getData(descriptor);
        if (media.isPresent()) {
            statsService.logStats("rest.media.download", new HashMap<>());

            Date expiryDate = new Date(System.currentTimeMillis() + MEDIA_EXPIRY_TIME);
            CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(false);
            return Response
                    .status(Response.Status.OK)
                    .type(media.get().getContentType())
                    .entity(media.get().getData())
                    .lastModified(media.get().getUpdated())
                    .expires(expiryDate)
                    .cacheControl(cacheControl)
                    .header("Content-Length", media.get().getData().length)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
