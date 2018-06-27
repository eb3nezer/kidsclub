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
    private String mediaDescriptor;
    private ImageCollectionDto imageCollection;

    public StudentTeamDto() {
    }

    public StudentTeamDto(
            Long id,
            ProjectDto project,
            String name,
            Integer score,
            List<UserDto> leaders,
            List<StudentDto> students,
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

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public List<StudentDto> getStudents() {
        return students;
    }

    public ImageCollectionDto getImageCollection() {
        return imageCollection;
    }

    public void setImageCollection(ImageCollectionDto imageCollection) {
        this.imageCollection = imageCollection;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public void setLeaders(List<UserDto> leaders) {
        this.leaders = leaders;
    }

    public void setStudents(List<StudentDto> students) {
        this.students = students;
    }
}
