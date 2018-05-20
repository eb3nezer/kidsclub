package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDocumentDto extends DtoObject {
    private Long id;
    private String name;
    private String description;
    private String mediaDescriptor;
    private String contentType;
    private ProjectDto project;
    private String icon;
    private String created;
    private String updated;

    public ProjectDocumentDto() {
    }

    public ProjectDocumentDto(
            Long id, String name, String description, String mediaDescriptor, String contentType, ProjectDto project,
            String icon,
            String created, String updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mediaDescriptor = mediaDescriptor;
        this.contentType = contentType;
        this.project = project;
        this.icon = icon;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public String getContentType() {
        return contentType;
    }

    public ProjectDto getProject() {
        return project;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getIcon() {
        return icon;
    }
}
