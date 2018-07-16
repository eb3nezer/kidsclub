package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceCountDto {
    private int totalStudents;
    private int totalSignedIn;
    private int totalNotSignedIn;

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalSignedIn() {
        return totalSignedIn;
    }

    public void setTotalSignedIn(int totalSignedIn) {
        this.totalSignedIn = totalSignedIn;
    }

    public int getTotalNotSignedIn() {
        return totalNotSignedIn;
    }

    public void setTotalNotSignedIn(int totalNotSignedIn) {
        this.totalNotSignedIn = totalNotSignedIn;
    }
}
