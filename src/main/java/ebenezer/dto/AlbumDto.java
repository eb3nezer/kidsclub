package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumDto extends DtoObject {
    private Long id;
    private String name;
    private String description;
    private ProjectDto project;
    private List<AlbumItemDto> items;
    private Boolean shared;
    private Long created;
    private Long updated;

    public AlbumDto() {
    }

    public AlbumDto(Long id, String name, String description, ProjectDto project, List<AlbumItemDto> items, Boolean shared, Long created, Long updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.project = project;
        this.items = items;
        this.shared = shared;
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

    public ProjectDto getProject() {
        return project;
    }

    public List<AlbumItemDto> getItems() {
        return items;
    }

    public Boolean getShared() {
        return shared;
    }

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }
}
