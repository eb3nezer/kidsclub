package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "project_documents")
@SequenceGenerator(initialValue = 1, name = "docgen", sequenceName = "project_document_sequence")
public class ProjectDocument extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @NotNull
    @Column(name = "content_type")
    private String contentType;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @Column(name = "created")
    private Long created;

    @NotNull
    @Column(name = "updated")
    private Long updated;

    public ProjectDocument() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public ProjectDocument(@NotNull Project project, @NotNull String name, String description, @NotNull String contentType, @NotNull String mediaDescriptor) {
        this();
        this.project = project;
        this.name = name;
        this.mediaDescriptor = mediaDescriptor;
        this.description = description;
        this.contentType = contentType;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public void updated() {
        updated = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public Project getProject() {
        return project;
    }

    public String getContentType() {
        return contentType;
    }
}
