package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "projects")
@SequenceGenerator(initialValue = 1, name = "projectgen", sequenceName = "project_sequence")
public class Project extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_projects",
            joinColumns = { @JoinColumn(name = "projectid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
    private Set<User> users = new HashSet<>();

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public Project() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public Project(@NotNull String name) {
        this();
        this.name = name;
    }

    public Project(@NotNull Long id, @NotNull String name) {
        this(name);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated.getTime();
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
