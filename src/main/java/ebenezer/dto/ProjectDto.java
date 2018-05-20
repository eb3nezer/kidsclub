package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto extends DtoObject {
    private Long id;
    private String name;
    private List<UserDto> users;

    public ProjectDto() {
    }

    public ProjectDto(Long id, String name, List<UserDto> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
