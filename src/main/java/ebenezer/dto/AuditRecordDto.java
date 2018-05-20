package ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class AuditRecordDto extends DtoObject {
    private Long id;
    private String change;
    private String changeTime;
    private UserDto user;
    private String auditLevel;
    private Long created;
    private Long updated;

    public AuditRecordDto() {
    }

    public AuditRecordDto(Long id, String change, String changeTime, UserDto user, String auditLevel, Long created, Long updated) {
        this.id = id;
        this.change = change;
        this.changeTime = changeTime;
        this.user = user;
        this.auditLevel = auditLevel;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public String getChange() {
        return change;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public UserDto getUser() {
        return user;
    }

    public String getAuditLevel() {
        return auditLevel;
    }

    public Long getCreated() {
        return created;
    }

    public Long getUpdated() {
        return updated;
    }
}
