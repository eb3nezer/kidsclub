package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumItemDto extends DtoObject {
    private Long id;
    private Integer order;
    private String description;
    private String mediaDescriptor;
    private Long created;
    private Long updated;

    public AlbumItemDto() {
    }

    public AlbumItemDto(Long id, Integer order, String description, String mediaDescriptor, Long created, Long updated) {
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

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }
}
