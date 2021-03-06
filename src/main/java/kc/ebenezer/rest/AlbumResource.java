package kc.ebenezer.rest;

import kc.ebenezer.Application;
import kc.ebenezer.dto.AlbumDto;
import kc.ebenezer.dto.mapper.AlbumItemMapper;
import kc.ebenezer.dto.mapper.AlbumMapper;
import kc.ebenezer.model.Album;
import kc.ebenezer.model.AlbumItem;
import kc.ebenezer.service.AlbumService;
import kc.ebenezer.service.StatsService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/albums")
@Transactional
public class AlbumResource {
    @Inject
    private AlbumMapper albumMapper;
    @Inject
    private AlbumItemMapper albumItemMapper;
    @Inject
    private AlbumService albumService;
    @Inject
    private StatsService statsService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAlbum(AlbumDto albumDto)  {
        // If the user didn't click on this field, then it will be null
        if (albumDto.getShared() == null) {
            albumDto.setShared(false);
        }
        Optional<Album> album = albumService.createAlbum(
                albumDto.getProject().getId(),
                albumDto.getName(),
                albumDto.getDescription(),
                albumDto.getShared());
        if (album.isPresent()) {
            logStats("rest.album.create", albumDto.getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(albumMapper.toDto(album.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumsForProject(@QueryParam("projectId") String projectId) {
        if (projectId == null) {
            throw new ValidationException("The projectId query parameter is required");
        }

        List<Album> albums = albumService.getAlbumsForProject(Long.valueOf(projectId));
        logStats("rest.albums.get", projectId);
        return Response.status(Response.Status.OK).entity(albumMapper.toDto(albums)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getAlbum(@PathParam("id") String id) {
        Optional<Album> album = albumService.getAlbumById(Long.valueOf(id));

        if (album.isPresent()) {
            logStats("rest.album.get", album.get().getProject().getId().toString());
            return Response.status(Response.Status.OK).entity(albumMapper.toDto(album.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{albumId}/photo/{photoId}")
    public Response deletePhoto(@PathParam("albumId") Long albumId,
                             @PathParam("photoId") Long photoId) {
        Optional<Album> album = albumService.getAlbumById(albumId);
        if (album.isPresent()) {
            Optional<AlbumItem> albumItem = albumService.deletePhotoFromAlbum(albumId, photoId);
            if (album.isPresent()) {
                logStats("rest.album.photo.delete", album.get().getProject().getId().toString());
                return Response.status(Response.Status.OK).entity(albumMapper.toDto(album.get())).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/upload/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
            @Context HttpServletRequest request,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("description") String description,
            @FormDataParam("shared") String shared,
            @PathParam("id") String albumId) throws IOException, ServletException {
        int fileSize = request.getContentLength();
        if (fileSize > Application.MAX_UPLOAD_SIZE) {
            throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
        }
        String filename = payloadDetail.getFileName().toLowerCase();

        Optional<AlbumItem> albumItem = albumService.addPhotoToAlbum(
                Long.valueOf(albumId), payload, fileSize, filename, Boolean.valueOf(shared), description);
        if (albumItem.isPresent()) {
            logStats("rest.album.photo.create", null);

            return Response.status(Response.Status.OK).entity(albumItemMapper.toDto(albumItem.get())).build();
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
