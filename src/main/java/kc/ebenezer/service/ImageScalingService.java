package kc.ebenezer.service;

import kc.ebenezer.dao.ImageCollectionDao;
import kc.ebenezer.dao.ImageDao;
import kc.ebenezer.model.*;
import kc.ebenezer.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Component
@Transactional
public class ImageScalingService {
    private static final Logger LOG = LoggerFactory.getLogger(ImageScalingService.class);

    @Inject
    private MediaService mediaService;
    @Inject
    private ImageDao imageDao;
    @Inject
    private ImageCollectionDao imageCollectionDao;

    private static Map<String, String> contentTypeToFormat;

    static {
        contentTypeToFormat = new HashMap<>();
        contentTypeToFormat.put("image/jpeg", "jpg");
        contentTypeToFormat.put("image/gif", "gif");
        contentTypeToFormat.put("image/png", "png");
    }

    private Optional<Media> getFullSize(String mediaDescriptorHint, ImageCollection imageCollection) {
        if (mediaDescriptorHint != null) {
            return mediaService.getData(mediaDescriptorHint);
        } else {
            if (imageCollection != null) {
                Optional<String> fullSizeDescriptor = imageCollection.getImages()
                    .stream()
                    .filter(i -> i.getSize() == ImageSize.DEFAULT)
                    .findFirst()
                    .map(Image::getMediaDescriptor);
                if (fullSizeDescriptor.isPresent()) {
                    return mediaService.getData(fullSizeDescriptor.get());
                }
            }
        }

        return Optional.empty();
    }

    private Optional<Media> getScaledMedia(BufferedImage fullSizeImage, int width, int height, String contentType, boolean shared, String description) {
        BufferedImage scaledImage = new BufferedImage(width, height, fullSizeImage.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(fullSizeImage, 0, 0, width, height, null);
        graphics2D.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = contentTypeToFormat.get(contentType);
        try {
            ImageIO.write(scaledImage, formatName, baos);
        } catch (IOException e) {
            LOG.error("Failed to write image to byte array", e);
            return Optional.empty();
        }
        Optional<Media> scaledMedia = mediaService.storeData(
            baos.toByteArray(),
            contentType,
            shared,
            description);

        return scaledMedia;
    }

    public void scaleImage(String mediaDescriptorHint, ImageCollection imageCollection) {
        List<ImageSize> missingSizes = new ArrayList<>(Arrays.asList(ImageSize.values()));
        for (Image image : imageCollection.getImages()) {
            missingSizes.remove(image.getSize());
        }

        if (missingSizes.isEmpty()) {
            return;
        }

        Optional<Media> fullSizeMedia = getFullSize(mediaDescriptorHint, imageCollection);

        if (!fullSizeMedia.isPresent()) {
            LOG.error("Unable to get full size image");
            return;
        }

        BufferedImage fullSizeImage;
        try {
            fullSizeImage = ImageIO.read(new ByteArrayInputStream(fullSizeMedia.get().getData()));
            int height = fullSizeImage.getHeight();
            int width = fullSizeImage.getWidth();
            for (ImageSize size : missingSizes) {
                LOG.info("Adding size " + size.getCode() + " for " + fullSizeMedia.get().getDescriptor());
                Image image = null;
                if (size == ImageSize.DEFAULT) {
                    image = new Image(imageCollection, size, fullSizeMedia.get().getDescriptor());
                } else {
                    double scaleFactor = (double) size.getMaxWidth() / (double) width;
                    int newHeight = Math.toIntExact(Math.round(height * scaleFactor));
                    int newWidth = size.getMaxWidth();
                    Optional<Media> scaledMedia = getScaledMedia(
                        fullSizeImage,
                        newWidth,
                        newHeight,
                        fullSizeMedia.get().getContentType(),
                        fullSizeMedia.get().isShared(),
                        fullSizeMedia.get().getDescription());

                    if (scaledMedia.isPresent()) {
                        image = new Image(imageCollection, size, scaledMedia.get().getDescriptor());
                    }
                }
                if (image != null) {
                    imageDao.create(image);
                    imageCollection.getImages().add(image);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to create image from data", e);
        }
        // end transaction
    }

    /**
     * Remove items from an object's image collection, if it has one.
     * @param photoUploadable The object to have its image collection removed
     */
    public void deleteOldImageCollection(PhotoUploadable photoUploadable) {
        if (photoUploadable.getImageCollection() != null && !photoUploadable.getImageCollection().getImages().isEmpty()) {
            for (Image image : photoUploadable.getImageCollection().getImages()) {
                mediaService.deleteData(image.getMediaDescriptor());
                imageDao.delete(image);
            }
            photoUploadable.getImageCollection().getImages().clear();
        }
    }

    public void repairOrCreateImageCollection(PhotoUploadable photoUploadable, String newMediaDescriptor) {
        if (photoUploadable.getImageCollection() == null) {
            ImageCollection imageCollection = new ImageCollection();
            imageCollectionDao.create(imageCollection);
            photoUploadable.setImageCollection(imageCollection);
        }
        scaleImage(newMediaDescriptor, photoUploadable.getImageCollection());
    }
}
