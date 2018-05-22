package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "students")
@SequenceGenerator(initialValue = 1, name = "studentgen", sequenceName = "student_sequence")
public class Student extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studentgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "school")
    private String school;

    @Column(name = "age")
    private Integer age;

    @Column(name = "school_year")
    private String schoolYear;

    @Column(name = "gender", length = 1)
    private String gender;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private StudentTeam studentTeam;

    public Student() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public Student(
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
            String schoolYear,
            Gender gender,
            Project project,
            StudentTeam studentTeam) {
        this();
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
        if (gender != null) {
            this.gender = gender.getCode();
        }
        this.project = project;
        this.studentTeam = studentTeam;
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

    public String getSchoolYear() {
        return schoolYear;
    }

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public void setUpdated(Date updated) {
        this.updated = updated.getTime();
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public StudentTeam getStudentTeam() {
        return studentTeam;
    }

    public void setStudentTeam(StudentTeam studentTeam) {
        this.studentTeam = studentTeam;
    }

    public Gender getGender() {
        return Gender.fromCode(gender);
    }

    public void setGender(Gender gender) {
        this.gender = gender.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", mediaDescriptor='" + mediaDescriptor + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", school='" + school + '\'' +
                ", age=" + age +
                ", schoolYear='" + schoolYear + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public static class StudentComparator implements Comparator<Student> {
        @Override
        public int compare(Student o1, Student o2) {
            String user1 = getCompareString(o1);
            String user2 = getCompareString(o2);

            return user1.compareTo(user2);
        }

        private String getCompareString(Student student) {
            String result = student.getName().toLowerCase();

            if (student.getFamilyName() != null && !student.getFamilyName().isEmpty()) {
                if (student.getGivenName() != null && !student.getGivenName().isEmpty()) {
                    result = student.getFamilyName().toLowerCase() + " " + student.getGivenName().toLowerCase();
                } else {
                    result = student.getFamilyName().toLowerCase();
                }
            }

            return result;
        }
    }

}
