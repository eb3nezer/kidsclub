package ebenezer.service;

import ebenezer.dao.MediaDao;
import ebenezer.model.Album;
import ebenezer.model.Media;
import ebenezer.model.User;
import ebenezer.permissions.SitePermission;
import ebenezer.rest.NoPermissionException;
import ebenezer.rest.ValidationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class MediaService {
    @Inject
    private MediaDao mediaDao;

    @Inject
    private UserService userService;

    private static Map<String, String> suffixToContenteType;

    static {
        suffixToContenteType = new HashMap<>();
        suffixToContenteType.put(".jpg", "image/jpeg");
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
    }

    public MediaService() {
    }

    public Optional<Media> storeData(InputStream inputStream, int maxSize, String filename, boolean shared, String description) throws IOException {
        byte[] data = new byte[maxSize];
        int bytesRead = 0;
        int chunk = 1024;
        int thisRead = 0;
        while ((thisRead = inputStream.read(data, bytesRead, chunk)) != -1) {
            bytesRead += thisRead;
        }
        byte[] actualData = new byte[bytesRead];
        System.arraycopy(data, 0, actualData, 0, bytesRead);
        String suffix = filename.substring(filename.lastIndexOf('.'), filename.length());
        String contentType = suffixToContenteType.get(suffix);
        if (contentType == null) {
            throw new ValidationException("Unrecognised file type");
        }

        return storeData(actualData, contentType, shared, description);
    }

    public Optional<Media> storeData(byte[] data, String contentType, boolean shared, String description) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            Media media = new Media(contentType, currentUser.get(), data, shared, description);
            Media inserted = mediaDao.create(media);
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
            throw new NoPermissionException("Anonymous cannot delete media");
        }

        Optional<Media> media = mediaDao.findByDescriptor(descriptor);
        if (!media.isPresent()) {
            throw new ValidationException("Could not find media for descriptor=" + descriptor);
        }

        if (media.get().getOwner().getId().equals(currentUser.get().getId()) ||
                SitePermissionService.userHasPermission(currentUser.get(), SitePermission.SYSTEM_ADMIN)) {
            mediaDao.delete(media.get());
        } else {
            throw new NoPermissionException("You do not have permission to delete this media");
        }
    }
}
