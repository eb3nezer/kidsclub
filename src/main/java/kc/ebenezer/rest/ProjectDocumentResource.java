package kc.ebenezer.rest;

import kc.ebenezer.Application;
import kc.ebenezer.dto.AlbumDto;
import kc.ebenezer.dto.ProjectDocumentDto;
import kc.ebenezer.dto.mapper.AlbumItemMapper;
import kc.ebenezer.dto.mapper.AlbumMapper;
import kc.ebenezer.dto.mapper.ProjectDocumentMapper;
import kc.ebenezer.dto.mapper.UserMapper;
import kc.ebenezer.model.Album;
import kc.ebenezer.model.AlbumItem;
import kc.ebenezer.model.ProjectDocument;
import kc.ebenezer.service.AlbumService;
import kc.ebenezer.service.ProjectDocumentService;
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
@Path("/documents")
@Transactional
public class ProjectDocumentResource {
    @Inject
    private ProjectDocumentMapper projectDocumentMapper;
    @Inject
    private StatsService statsService;
    @Inject
    private ProjectDocumentService projectDocumentService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentsForProject(@QueryParam("projectId") Long projectId) {
        if (projectId == null) {
            throw new ValidationException("The projectId query parameter is required");
        }

        List<ProjectDocument> projectDocuments = projectDocumentService.getDocumentsForProject(projectId);
        logStats("rest.documents.get", projectId.toString());
        return Response.status(Response.Status.OK).entity(projectDocumentMapper.toDto(projectDocuments)).build();
    }


    @POST
    @Path("/upload/{projectId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
            @Context HttpServletRequest request,
            @FormDataParam("file") InputStream payload,
            @FormDataParam("file") FormDataContentDisposition payloadDetail,
            @FormDataParam("description") String description,
            @PathParam("projectId") Long projectId) throws IOException, ServletException {
        int fileSize = request.getContentLength();
        if (fileSize > Application.MAX_UPLOAD_SIZE) {
            throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
        }
        String filename = payloadDetail.getFileName().toLowerCase();

        Optional<ProjectDocument> projectDocument = projectDocumentService.addDocumentToProject(projectId, payload, fileSize, filename, description);
        if (projectDocument.isPresent()) {
            logStats("rest.document.create", projectId.toString());

            return Response.status(Response.Status.OK).entity(projectDocumentMapper.toDto(projectDocument.get())).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteDocument(ProjectDocumentDto projectDocumentDto) {
        Long projectId = projectDocumentDto.getProject().getId();
        Long documentId = projectDocumentDto.getId();

        projectDocumentService.deleteProjectDocument(projectId, documentId);
        logStats("rest.document.delete", projectId.toString());

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{documentId}/project/{projectId}")
    public Response deleteDocument(@PathParam("documentId") Long documentId, @PathParam("projectId") Long projectId) {
        projectDocumentService.deleteProjectDocument(projectId, documentId);
        logStats("rest.document.delete", projectId.toString());

        return Response.status(Response.Status.OK).build();
    }

    private void logStats(String key, String projectId) {
        Map<String, String> metadata = new HashMap<>();
        if (projectId != null) {
            metadata.put("PROJECT", projectId);
        }
        statsService.logStats(key, metadata);
    }
}
