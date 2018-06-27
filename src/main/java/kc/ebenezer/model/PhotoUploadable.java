package kc.ebenezer.model;

/**
 * Something that can have a media descriptor and {@link ImageCollection}.
 */
public interface PhotoUploadable {
    /**
     * Set the media descriptor for this object's full size image.
     * @param mediaDescriptor The media descriptor for this object's full sized image. This may be null
     *                        to indicate that this object does not have a media descriptor.
     * @see Media#getDescriptor()
     */
    void setMediaDescriptor(String mediaDescriptor);

    /**
     * Get the media descriptor for this object's full size image.
     * @return The media descriptor, or null if this object does not have a media descriptor.
     */
    String getMediaDescriptor();

    /**
     * Set this object's image collection.
     * @param imageCollection The image collection, or null to indicate that this object does not have an image
     *                        collection.
     */
    void setImageCollection(ImageCollection imageCollection);

    /**
     * Get this object's image collection.
     * @return The image collection, or null if this object does not have an image collection.
     */
    ImageCollection getImageCollection();
}
