package kc.ebenezer.model;

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

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_relationship")
    private String contactRelationship;

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

    @Column(name = "special_instructions", length = 1024)
    private String specialInstructions;

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

    @Column(name = "media_permitted")
    private Boolean mediaPermitted;

    @ManyToOne
    @JoinColumn(name = "attendance_snapshot")
    private AttendanceRecord attendanceSnapshot;

    public Student() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public Student(
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
            String schoolYear,
            Gender gender,
            String specialInstructions,
            Boolean mediaPermitted,
            Project project,
            StudentTeam studentTeam,
            AttendanceRecord attendanceSnapshot) {
        this();
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
        if (gender != null) {
            this.gender = gender.getCode();
        }
        this.specialInstructions = specialInstructions;
        this.project = project;
        this.studentTeam = studentTeam;
        this.mediaPermitted = mediaPermitted;
        this.attendanceSnapshot = attendanceSnapshot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
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
        if (gender == null) {
            this.gender = null;
        } else {
            this.gender = gender.getCode();
        }
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
    }

    public Boolean getMediaPermitted() {
        return mediaPermitted;
    }

    public Boolean isMediaPermitted() {
        return mediaPermitted;
    }

    public void setMediaPermitted(Boolean mediaPermitted) {
        this.mediaPermitted = mediaPermitted;
    }

    public AttendanceRecord getAttendanceSnapshot() {
        return attendanceSnapshot;
    }

    public void setAttendanceSnapshot(AttendanceRecord attendanceSnapshot) {
        this.attendanceSnapshot = attendanceSnapshot;
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
                ", mediaDescriptor='" + mediaDescriptor + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactRelationship='" + contactRelationship + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", school='" + school + '\'' +
                ", age=" + age +
                ", schoolYear='" + schoolYear + '\'' +
                ", gender='" + gender + '\'' +
                ", specialInstructions='" + specialInstructions + '\'' +
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
