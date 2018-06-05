package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceRecordDto extends DtoObject {
    private Long id;
    private StudentDto student;
    private String attendanceType;
    private String attendanceCode;
    private String recordTime;
    private UserDto verifier;
    private String comment;

    public AttendanceRecordDto() {
    }

    public AttendanceRecordDto(Long id, StudentDto student, String attendanceType, String attendanceCode, String recordTime, UserDto verifier, String comment) {
        this.id = id;
        this.student = student;
        this.attendanceType = attendanceType;
        this.attendanceCode = attendanceCode;
        this.recordTime = recordTime;
        this.verifier = verifier;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentDto getStudent() {
        return student;
    }

    public void setStudent(StudentDto student) {
        this.student = student;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public UserDto getVerifier() {
        return verifier;
    }

    public void setVerifier(UserDto verifier) {
        this.verifier = verifier;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAttendanceCode() {
        return attendanceCode;
    }

    public void setAttendanceCode(String attendanceCode) {
        this.attendanceCode = attendanceCode;
    }
}
