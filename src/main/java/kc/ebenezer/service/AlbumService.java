package kc.ebenezer.service;

import kc.ebenezer.dao.AlbumDao;
import kc.ebenezer.dao.AlbumItemDao;
import kc.ebenezer.model.*;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AlbumService {
    @Inject
    private AlbumDao albumDao;

    @Inject
    private AlbumItemDao albumItemDao;

    @Inject
    private UserService userService;

    @Inject
    private MediaService mediaService;

    @Inject
    private AuditService auditService;

    @Inject
    private ProjectService projectService;

    public Optional<Album> createAlbum(Long projectId, String name, String description, boolean shared) {
        Optional<Album> result;

        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot create albums");
        }

        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Could not access project for id=" + projectId);
        }

        if (name == null || name.isEmpty()) {
            throw new ValidationException("The album name cannot be blank");
        }

        Album album = new Album(name, description, project.get(), shared);
        auditService.audit("Created new photo album \"" + name + "\"", album.getCreated());
        result = Optional.ofNullable(albumDao.create(album));

        return result;
    }

    public List<Album> getAlbumsForProject(Long projectId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot retrieve albums");
        }

        Optional<Project> project = projectService.getProjectById(currentUser.get(), projectId);
        if (!project.isPresent()) {
            throw new ValidationException("Could not access project for id=" + projectId);
        }

        List<Album> albums = albumDao.getAlbumsForProject(projectId);

        return albums;
    }

    public Optional<Album> getAlbumById(Long albumId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            throw new NoPermissionException("Anonymous cannot retrieve albums");
        }

        Optional<Album> album = albumDao.findById(albumId);
        if (album.isPresent()) {
            if (!(album.get().isShared() || ProjectPermissionService.userIsProjectMember(currentUser.get(), album.get().getProject()))) {
                throw new NoPermissionException("User does not have permission to read albums in this project");
            }
        }

        return album;
    }

    public Optional<AlbumItem> addPhotoToAlbum(Long albumId, InputStream inputStream, int maxSize, String filename, boolean shared, String description) throws IOException {
        Optional<AlbumItem> result = Optional.empty();
        Optional<Album> album = albumDao.findById(albumId);
        if (album.isPresent()) {
            Optional<Media> media = mediaService.storeData(inputStream, maxSize, filename, shared, description);
            if (media.isPresent()) {
                String contentType = media.get().getContentType();
                if (!contentType.contains("image")) {
                    throw new ValidationException("Only images can be added to albums");
                }
                int newOrderInAlbum = 0;
                for (AlbumItem albumItem : album.get().getItems()) {
                    albumItem.setOrder(newOrderInAlbum++);
                }
                AlbumItem newAlbumItem = new AlbumItem(newOrderInAlbum, media.get().getDescriptor(), description);
                newAlbumItem = albumItemDao.create(newAlbumItem);
                album.get().getItems().add(newAlbumItem);
                album.get().updated();
                auditService.audit("Added new photo \"" + media.get().getDescriptor() + "\" to album id=" + albumId,
                        album.get().getUpdated());
                result = Optional.of(newAlbumItem);
            } else {
                throw new ValidationException("Failed to create media");
            }
        }

        return result;
    }
}
