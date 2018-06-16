package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student_teams")
@SequenceGenerator(name = "steamgen", sequenceName = "student_team_sequence")
public class StudentTeam extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steamgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "points")
    private Integer score;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "student_team_leaders",
            joinColumns = { @JoinColumn(name = "student_team_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> leaders = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Set<Student> students = new HashSet<>();

    public StudentTeam() {
        created = System.currentTimeMillis();
        updated = created;
    }

    public StudentTeam(
            @NotNull Project project,
            @NotNull String name,
            @NotNull Integer score,
            Set<User> leaders,
            Set<Student> students,
            String avatarUrl,
            String mediaDescriptor,
            Integer sortOrder) {
        this();

        this.project = project;
        this.name = name;
        this.score = score;
        this.leaders = leaders;
        this.students = students;
        this.avatarUrl = avatarUrl;
        this.mediaDescriptor = mediaDescriptor;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer newValue) {
        score = newValue;
    }

    public Set<User> getLeaders() {
        return leaders;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Integer getSortOrder() {
        if (sortOrder == null) {
            return 0;
        }
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Date getUpdated() {
        return new Date(updated);
    }

    public void updated() {
        this.updated = System.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    public static class StudentTeamComparator implements Comparator<StudentTeam> {
        @Override
        public int compare(StudentTeam o1, StudentTeam o2) {
            return o1.getSortOrder().compareTo(o2.getSortOrder());
        }
    }

    public static class StudentTeamScoreComparator implements Comparator<StudentTeam> {
        @Override
        public int compare(StudentTeam o1, StudentTeam o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }
}
