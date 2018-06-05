package kc.ebenezer.model;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Set<ProjectProperty> projectProperties;

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

    public Set<ProjectProperty> getProjectProperties() {
        return projectProperties;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setProjectProperties(Set<ProjectProperty> projectProperties) {
        this.projectProperties = projectProperties;
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
