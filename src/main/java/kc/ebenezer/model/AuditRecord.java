package kc.ebenezer.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "audit_records")
@SequenceGenerator(initialValue = 1, name = "auditgen", sequenceName = "audit_sequence")
public class AuditRecord extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auditgen")
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "change", length = 1024)
    private String change;

    @NonNull
    @Column(name = "change_time")
    private Long changeTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "audit_level")
    private String auditLevel;

    @NonNull
    @Column(name = "created")
    private Long created;

    @NonNull
    @Column(name = "updated")
    private Long updated;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public AuditRecord() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public AuditRecord(String change, Date changeTime, User user, AuditLevel auditLevel, Project project) {
        this();
        this.change = change;
        this.changeTime = changeTime.getTime();
        this.user = user;
        this.auditLevel = auditLevel.getCode();
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public String getChange() {
        return change;
    }

    public Date getChangeTime() {
        return new Date(changeTime);
    }

    public User getUser() {
        return user;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public void setCreated(Date created) {
        this.created = created.getTime();
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public void setUpdated(Date updated) {
        this.updated = updated.getTime();
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public AuditLevel getAuditLevel() {
        if (auditLevel == null) {
            return null;
        }
        return AuditLevel.forCode(auditLevel);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime.getTime();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditRecord that = (AuditRecord) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
