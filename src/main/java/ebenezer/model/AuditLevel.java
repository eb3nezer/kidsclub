package ebenezer.model;

import java.util.HashMap;
import java.util.Map;

public enum AuditLevel {
    DEBUG("D"),
    INFO("I"),
    WARN("W"),
    ERROR("E");

    private String code;
    private static Map<String, AuditLevel> codeMap;

    static {
        codeMap = new HashMap<>();
        for (AuditLevel auditLevel : AuditLevel.values()) {
            codeMap.put(auditLevel.getCode(), auditLevel);
        }
    }

    private AuditLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AuditLevel forCode(String code) {
        return codeMap.get(code);
    }
}
