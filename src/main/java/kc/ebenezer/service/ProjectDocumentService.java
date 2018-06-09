package kc.ebenezer.service;

import kc.ebenezer.dao.ProjectDocumentDao;
import kc.ebenezer.model.Media;
import kc.ebenezer.model.Project;
import kc.ebenezer.model.ProjectDocument;
import kc.ebenezer.model.User;
import kc.ebenezer.permissions.ProjectPermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectDocumentService {
    @Inject
    private ProjectDocumentDao projectDocumentDao;
    @Inject
    private UserService userService;
    @Inject
    private MediaService mediaService;
    @Inject
    private AuditService auditService;
    @Inject
    private ProjectService projectService;
    @Inject
    private ProjectPermissionService projectPermissionService;

    public List<ProjectDocument> getDocumentsForProject(Long projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot retrieve documents");
        }

        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Could not access project for id=" + projectId);
        }

        return projectDocumentDao.getDocumentsForProject(projectId);
    }

    public Optional<ProjectDocument> addDocumentToProject(Long projectId, InputStream inputStream, int maxSize, String filename, String description) throws IOException {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create documents");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project ID");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_DOCUMENTS)) {
            throw new NoPermissionException("You do not have permission to create documents");
        }

        Optional<Media> media = mediaService.storeData(inputStream, maxSize, filename, false, description);
        if (media.isPresent()) {
            ProjectDocument result = new ProjectDocument(project.get(), filename, description, media.get().getContentType(), media.get().getDescriptor());
            result = projectDocumentDao.create(result);
            auditService.audit(project.get(), "Created document id=" + result.getId() + " in project id=" + result.getProject().getId() +
                            " with filename=\"" + result.getName() + "\"",
                    new Date());
            return Optional.of(result);
        } else {
            throw new ValidationException("Failed to create document");
        }
    }

    public void deleteProjectDocument(Long projectId, Long documentId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create documents");
        }
        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Invalid project ID");
        }
        if (!projectPermissionService.userHasPermission(currentUser.get(), project.get(), ProjectPermission.EDIT_DOCUMENTS)) {
            throw new NoPermissionException("You do not have permission to create documents");
        }
        Optional<ProjectDocument> document = projectDocumentDao.findById(documentId);
        if (!document.isPresent()) {
            throw new ValidationException("Invalid document ID");
        }
        if (!document.get().getProject().getId().equals(projectId)) {
            throw new NoPermissionException("The specified document does not belong to the specified project");
        }

        projectDocumentDao.delete(document.get());
        auditService.audit(project.get(), "Deleted document id=" + documentId + " in project id=" + projectId,
                new Date());
    }
}
