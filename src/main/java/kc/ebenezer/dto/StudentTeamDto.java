package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentTeamDto extends DtoObject {
    private Long id;
    private ProjectDto project;
    private String name;
    private Integer score;
    private Long created;
    private Long updated;
    private List<UserDto> leaders;
    private List<StudentDto> students;
    private String avatarUrl;
    private String mediaDescriptor;

    public StudentTeamDto() {
    }

    public StudentTeamDto(
            Long id,
            ProjectDto project,
            String name,
            Integer score,
            List<UserDto> leaders,
            List<StudentDto> students,
            String avatarUrl,
            String mediaDescriptor,
            Long created,
            Long updated) {
        this.id = id;
        this.project = project;
        this.name = name;
        this.score = score;
        this.created = created;
        this.updated = updated;
        this.leaders = leaders;
        this.students = students;
        this.avatarUrl = avatarUrl;
        this.mediaDescriptor = mediaDescriptor;
    }

    public Long getId() {
        return id;
    }

    public ProjectDto getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }

    public List<UserDto> getLeaders() {
        return leaders;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public List<StudentDto> getStudents() {
        return students;
    }
}
