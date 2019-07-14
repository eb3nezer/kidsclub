package kc.ebenezer.service;

import kc.ebenezer.Application;
import kc.ebenezer.dao.MediaDao;
import kc.ebenezer.model.Album;
import kc.ebenezer.model.Media;
import kc.ebenezer.model.User;
import kc.ebenezer.permissions.SitePermission;
import kc.ebenezer.rest.NoPermissionException;
import kc.ebenezer.rest.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class MediaService {
    private static final Logger LOG = LoggerFactory.getLogger(MediaService.class);

    @Inject
    private MediaDao mediaDao;
    @Inject
    private UserService userService;
    @Inject
    private AuditService auditService;

    private static Map<String, String> suffixToContenteType;

    static {
        suffixToContenteType = new HashMap<>();
        suffixToContenteType.put(".jpg", "image/jpeg");
        suffixToContenteType.put(".jpeg", "image/jpeg");
        suffixToContenteType.put(".gif", "image/gif");
        suffixToContenteType.put(".png", "image/png");
        suffixToContenteType.put(".pdf", "application/pdf");
        suffixToContenteType.put(".doc", "application/msword");
        suffixToContenteType.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        suffixToContenteType.put(".xls", "application/vnd.ms-excel");
        suffixToContenteType.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        suffixToContenteType.put(".ppt", "application/vnd.ms-powerpoint");
        suffixToContenteType.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        suffixToContenteType.put(".key", "application/vnd.apple.keynote");
        suffixToContenteType.put(".csv", "text/csv");
        suffixToContenteType.put(".html", "text/html");
    }

    public MediaService() {
    }

    public Optional<Media> storeData(InputStream inputStream, int maxSize, String filename, boolean shared, String description) throws IOException {
        if (maxSize > Application.MAX_UPLOAD_SIZE) {
            throw new ValidationException("File size must be smaller than " + Application.MAX_UPLOAD_SIZE_DESCRIPTION);
        }

        String contentType = getMIMETypeForFileName(filename);
        if (contentType == null) {
            throw new ValidationException("Unrecognised file type");
        }

        byte[] data = new byte[maxSize];
        int bytesRead = 0;
        int chunk = 1024;
        int thisRead = 0;
        while ((thisRead = inputStream.read(data, bytesRead, chunk)) != -1) {
            bytesRead += thisRead;
        }
        byte[] actualData = new byte[bytesRead];
        System.arraycopy(data, 0, actualData, 0, bytesRead);

        return storeData(actualData, contentType, shared, description);
    }

    public String getMIMETypeForSuffix(String suffix) {
        return suffixToContenteType.get(suffix);
    }

    public String getMIMETypeForFileName(String filename) {
        if (filename == null) {
            return null;
        }
        String suffix = filename.toLowerCase().substring(filename.lastIndexOf('.'), filename.length());
        return suffixToContenteType.get(suffix);
    }

    public Optional<Media> storeData(byte[] data, String contentType, boolean shared, String description) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            Media media = new Media(contentType, currentUser.get(), data, shared, description);
            Media inserted = mediaDao.create(media);
            auditService.audit(null, "Stored " + data.length + " bytes of media of type " + contentType, new Date());
            return Optional.of(inserted);
        }

        return Optional.empty();
    }

    public Optional<Media> getData(String descriptor) {
        Optional<Media> media = mediaDao.findByDescriptor(descriptor);
        return media;
    }

    public void deleteData(String descriptor) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (!currentUser.isPresent()) {
            LOG.error("Anonymous cannot delete media");
            throw new NoPermissionException("Anonymous cannot delete media");
        }

        Optional<Media> media = mediaDao.findByDescriptor(descriptor);
        if (!media.isPresent()) {
            // OK it's already gone
            return;
        }

        if (media.get().getOwner().getId().equals(currentUser.get().getId()) ||
                SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN)) {
            mediaDao.delete(media.get());
        } else {
            LOG.error("Media " + media.get().getDescriptor() + " is owned by " + media.get().getOwner().getId() +
                " and user " + currentUser.get().getId() + " does not have the permission " + SitePermission.SYSTEM_ADMIN +
                " in deleteData()");
            throw new NoPermissionException("You do not have permission to delete this media");
        }
    }
}
