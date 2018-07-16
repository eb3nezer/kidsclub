package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceDetailsDto {
    private AttendanceCountDto attendanceCount;
    private List<AttendanceRecordDto> attendanceRecords;

    public AttendanceCountDto getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(AttendanceCountDto attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public List<AttendanceRecordDto> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(List<AttendanceRecordDto> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }
}
