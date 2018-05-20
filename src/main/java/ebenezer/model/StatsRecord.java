package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "stats_records")
@SequenceGenerator(initialValue = 1, name = "statsgen", sequenceName = "stats_sequence")
public class StatsRecord extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statsgen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "event_key")
    private String eventKey;

    @NotNull
    @Column(name = "created")
    private Long created;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stats_id")
    private Set<StatsMetadata> metadata = new HashSet<>();


    public StatsRecord() {
        created = System.currentTimeMillis();
    }

    public StatsRecord(String eventKey) {
        this();
        this.eventKey = eventKey;
    }

    public Long getId() {
        return id;
    }

    public String getEventKey() {
        return eventKey;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public Set<StatsMetadata> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsRecord)) return false;
        StatsRecord that = (StatsRecord) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
