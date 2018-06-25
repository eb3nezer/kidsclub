package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumItemDto extends DtoObject {
    private Long id;
    private Integer order;
    private String description;
    private String mediaDescriptor;
    private String created;
    private String updated;
    private ImageCollectionDto imageCollection;

    public AlbumItemDto() {
    }

    public AlbumItemDto(Long id, Integer order, String description, String mediaDescriptor, String created, String updated) {
        this.id = id;
        this.order = order;
        this.description = description;
        this.mediaDescriptor = mediaDescriptor;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public Integer getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public ImageCollectionDto getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(ImageCollectionDto imageCollection) {
        this.imageCollection = imageCollection;
    }
}
