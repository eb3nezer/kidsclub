package kc.ebenezer.model;

import java.util.HashMap;
import java.util.Map;

public enum AttendanceType {
    SIGN_IN("I", "Sign In"),
    SIGN_OUT("O", "Sign Out"),
    ABSENT("A", "Absent"),
    OUTSIDE("S", "Outside"),
    NO_RECORD("N", "No Record");

    private String code;
    private String name;

    private static Map<String, AttendanceType> codeMap = new HashMap<>();
    private static Map<String, AttendanceType> nameMap = new HashMap<>();

    static {
        for (AttendanceType attendanceType : AttendanceType.values()) {
            codeMap.put(attendanceType.code, attendanceType);
            nameMap.put(attendanceType.name, attendanceType);
        }
    }
    private AttendanceType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AttendanceType getForCode(String code) {
        return codeMap.get(code);
    }

    public static AttendanceType getForName(String name) {
        return nameMap.get(name);
    }
}
