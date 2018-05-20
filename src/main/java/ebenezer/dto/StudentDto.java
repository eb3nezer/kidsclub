package ebenezer.dto;

import ebenezer.model.Project;
import ebenezer.model.StudentTeam;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentDto extends DtoObject {
    private Long id;
    private String name;
    private String givenName;
    private String familyName;
    private String avatarUrl;
    private String mediaDescriptor;
    private String email;
    private String phone;
    private String school;
    private Integer age;
    private Integer schoolYear;
    private Long projectId;
    private StudentTeamDto studentTeam;
    private Long created;
    private Long updated;

    public StudentDto() {
    }

    public StudentDto(Long id) {
        this.id = id;
    }


    public StudentDto(
            Long id,
            String name,
            String givenName,
            String familyName,
            String avatarUrl,
            String mediaDescriptor,
            String email,
            String phone,
            String school,
            Integer age,
            Integer schoolYear,
            Long projectId,
            StudentTeamDto studentTeam,
            Long created,
            Long updated) {
        this.id = id;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.avatarUrl = avatarUrl;
        this.mediaDescriptor = mediaDescriptor;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.age = age;
        this.schoolYear = schoolYear;
        this.projectId = projectId;
        this.studentTeam = studentTeam;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSchool() {
        return school;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getSchoolYear() {
        return schoolYear;
    }

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public Long getProjectId() {
        return projectId;
    }

    public StudentTeamDto getStudentTeam() {
        return studentTeam;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
