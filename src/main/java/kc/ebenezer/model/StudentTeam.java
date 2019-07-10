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
public class StudentTeam extends ModelObject implements PhotoUploadable {
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

    @Column(name = "media_descriptor")
    private String mediaDescriptor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "images_id")
    private ImageCollection imageCollection;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "scoring")
    private Boolean scoring;

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
        imageCollection = new ImageCollection();
    }

    public StudentTeam(
            @NotNull Project project,
            @NotNull String name,
            @NotNull Integer score,
            Set<User> leaders,
            Set<Student> students,
            String mediaDescriptor,
            Integer sortOrder,
            Boolean scoring) {
        this();

        this.project = project;
        this.name = name;
        this.score = score;
        this.leaders = leaders;
        this.students = students;
        this.mediaDescriptor = mediaDescriptor;
        this.sortOrder = sortOrder;
        this.scoring = scoring;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setLeaders(Set<User> leaders) {
        this.leaders = leaders;
    }

    @Override
    public String getMediaDescriptor() {
        return mediaDescriptor;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
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

    public Boolean getScoring() {
        if (scoring == null) {
            return false;
        }
        return scoring;
    }

    public void setScoring(Boolean scoring) {
        this.scoring = scoring;
    }

    @Override
    public void setMediaDescriptor(String mediaDescriptor) {
        this.mediaDescriptor = mediaDescriptor;
    }

    @Override
    public ImageCollection getImageCollection() {
        return imageCollection;
    }

    @Override
    public void setImageCollection(ImageCollection imageCollection) {
        this.imageCollection = imageCollection;
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
