package kc.ebenezer.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditRecordDto extends DtoObject {
    private Long id;
    private String change;
    private String changeTime;
    private UserDto user;
    private String auditLevel;
    private ProjectDto project;
    private Long created;
    private Long updated;

    public AuditRecordDto() {
    }

    public AuditRecordDto(Long id, String change, String changeTime, UserDto user, String auditLevel, ProjectDto project, Long created, Long updated) {
        this.id = id;
        this.change = change;
        this.changeTime = changeTime;
        this.user = user;
        this.auditLevel = auditLevel;
        this.created = created;
        this.updated = updated;
        this.project = project;
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

    public ProjectDto getProject() {
        return project;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }
}
