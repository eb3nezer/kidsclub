package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "albums")
@SequenceGenerator(initialValue = 1, name = "albumgen", sequenceName = "album_sequence")
public class Album extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "albumgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = AlbumItem.class)
    @JoinColumn(name = "album_id")
    @OrderBy("order asc")
    private List<AlbumItem> items;

    @NotNull
    @Column(name = "shared")
    private Boolean shared;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public Album() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public Album(@NotNull String name, String description, @NotNull Project project, @NotNull Boolean shared) {
        this();
        this.name = name;
        this.description = description;
        this.project = project;
        this.shared = shared;
        items = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Project getProject() {
        return project;
    }

    public List<AlbumItem> getItems() {
        return items;
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

    public Boolean getShared() {
        return shared;
    }

    public Boolean isShared() {
        return shared;
    }
}
