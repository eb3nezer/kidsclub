package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto extends DtoObject {
    private Long id;
    private String name;
    private List<UserDto> users;
    private Map<String, String> properties;

    public ProjectDto() {
    }

    public ProjectDto(Long id, String name, List<UserDto> users, Map<String, String> properties) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.properties = properties;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
