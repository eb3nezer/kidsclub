package ebenezer.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "audit_records")
@SequenceGenerator(initialValue = 1, name = "auditgen", sequenceName = "audit_sequence")
public class AuditRecord extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auditgen")
    @Column(name = "id")
    private Long id;

    @Column(name = "change", length = 1024)
    private String change;

    @Column(name = "change_time")
    private Long changeTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "audit_level")
    private String auditLevel;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public AuditRecord() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public AuditRecord(String change, Date changeTime, User user, AuditLevel auditLevel) {
        this();
        this.change = change;
        this.changeTime = changeTime.getTime();
        this.user = user;
        this.auditLevel = auditLevel.getCode();
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
}
