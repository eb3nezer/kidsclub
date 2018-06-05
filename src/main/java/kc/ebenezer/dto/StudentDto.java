package kc.ebenezer.dto;

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
    private String mediaDescriptor;
    private String contactName;
    private String contactRelationship;
    private String email;
    private String phone;
    private String school;
    private Integer age;
    private String schoolYear;
    private String gender;
    private String specialInstructions;
    private Boolean mediaPermitted;
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
            String mediaDescriptor,
            String contactName,
            String contactRelationship,
            String email,
            String phone,
            String school,
            Integer age,
            String gender,
            String specialInstructions,
            Boolean mediaPermitted,
            String schoolYear,
            Long projectId,
            StudentTeamDto studentTeam,
            Long created,
            Long updated) {
        this.id = id;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.mediaDescriptor = mediaDescriptor;
        this.contactName = contactName;
        this.contactRelationship = contactRelationship;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.age = age;
        this.schoolYear = schoolYear;
        this.gender = gender;
        this.specialInstructions = specialInstructions;
        this.projectId = projectId;
        this.studentTeam = studentTeam;
        this.created = created;
        this.updated = updated;
        this.mediaPermitted = mediaPermitted;
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

    public String getSchoolYear() {
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

    public String getGender() {
        return gender;
    }

    public String getContactName() {
        return contactName;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStudentTeam(StudentTeamDto studentTeam) {
        this.studentTeam = studentTeam;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Boolean getMediaPermitted() {
        return mediaPermitted;
    }

    public void setMediaPermitted(Boolean mediaPermitted) {
        this.mediaPermitted = mediaPermitted;
    }
}
