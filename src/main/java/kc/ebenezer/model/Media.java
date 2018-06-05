package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "media")
public class Media extends ModelObject {
    @Id
    @NotNull
    @Column(name = "descriptor")
    private String descriptor;

    @NotNull
    @Column(name = "content_type")
    private String contentType;

    @Column(name = "data")
    @Lob
    private byte[] data;

    @NotNull
    @Column(name = "shared")
    private Boolean shared;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "created")
    private Long created;

    @NotNull
    @Column(name = "updated")
    private Long updated;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Media() {
        created = System.currentTimeMillis();
        updated = created;
        generateDescriptor();
    }

    public Media(String contentType, User owner, byte[] data, Boolean shared, String description) {
        this();
        this.contentType = contentType;
        this.owner = owner;
        this.data = data;
        this.shared = shared;
        this.description = description;
        generateDescriptor();
    }

    public String getDescriptor() {
        return descriptor;
    }

    private void generateDescriptor() {
        UUID uuid = UUID.randomUUID();
        descriptor = uuid.toString();
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public User getOwner() {
        return owner;
    }

    public Boolean getShared() {
        return shared;
    }

    public Boolean isShared() {
        return shared;
    }

    public String getDescription() {
        return description;
    }
}
