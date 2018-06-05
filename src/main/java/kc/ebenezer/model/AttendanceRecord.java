package kc.ebenezer.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "attendance")
@SequenceGenerator(initialValue = 1, name = "attendancegen", sequenceName = "attendance_sequence")
public class AttendanceRecord extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendancegen")
    @Column(name = "id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @NonNull
    @Column(name = "attendance_type", length = 1)
    private String attendanceType;

    @NonNull
    @Column(name = "record_time")
    private Long recordTime;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verifier_id")
    private User verifier;

    @Column(name = "comment")
    private String comment;

    public AttendanceRecord() {
    }

    public AttendanceRecord(Student student, AttendanceType attendanceType, Date recordTime, User verifier, String comment) {
        this.student = student;
        this.attendanceType = attendanceType.getCode();
        this.recordTime = recordTime.getTime();
        this.verifier = verifier;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AttendanceType getAttendanceType() {
        return AttendanceType.getForCode(attendanceType);
    }

    public void setAttendanceType(@NotNull AttendanceType attendanceType) {
        this.attendanceType = attendanceType.getCode();
    }

    public Date getRecordTime() {
        return new Date(recordTime);
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime.getTime();
    }

    public User getVerifier() {
        return verifier;
    }

    public void setVerifier(User verifier) {
        this.verifier = verifier;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
