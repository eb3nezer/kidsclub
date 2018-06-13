package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "stats_metadata")
@SequenceGenerator(initialValue = 1, name = "statsmetagen", sequenceName = "stats_meta_sequence")
public class StatsMetadata extends ModelObject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statsmetagen")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "data_key")
    private String dataKey;

    @NotNull
    @Column(name = "dataValue")
    private String dataValue;

    public StatsMetadata() {
    }

    public StatsMetadata(@NotNull String dataKey, @NotNull String dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    public String getDataKey() {
        return dataKey;
    }

    public String getDataValue() {
        return dataValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsMetadata metadata = (StatsMetadata) o;
        return Objects.equals(id, metadata.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
