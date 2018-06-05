package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class StatsMetadata extends ModelObject {
    @EmbeddedId
    private StatsMetadataPK key;

    public StatsMetadata() {
    }

    public StatsMetadata(@NotNull String dataKey, @NotNull String dataValue, @NotNull StatsRecord statsRecord) {
        key = new StatsMetadataPK(dataKey, dataValue, statsRecord);
    }

    public String getDataKey() {
        return key.getDataKey();
    }

    public String getDataValue() {
        return key.getDataValue();
    }

    public StatsRecord getStatsRecord() {
        return key.getStatsRecord();
    }

    @Embeddable
    public static class StatsMetadataPK implements Serializable {
        @NotNull
        @Column(name = "data_key")
        private String dataKey;

        @NotNull
        @Column(name = "dataValue")
        private String dataValue;

        @NotNull
        @ManyToOne
        @JoinColumn(name = "stats_id")
        private StatsRecord statsRecord;

        public StatsMetadataPK() {
        }

        public StatsMetadataPK(@NotNull String dataKey, @NotNull String dataValue, @NotNull StatsRecord statsRecord) {
            this.dataKey = dataKey;
            this.dataValue = dataValue;
            this.statsRecord = statsRecord;
        }

        public String getDataKey() {
            return dataKey;
        }

        public String getDataValue() {
            return dataValue;
        }

        public StatsRecord getStatsRecord() {
            return statsRecord;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StatsMetadataPK)) return false;
            StatsMetadataPK that = (StatsMetadataPK) o;
            return Objects.equals(getDataKey(), that.getDataKey()) &&
                    Objects.equals(getDataValue(), that.getDataValue()) &&
                    Objects.equals(getStatsRecord(), that.getStatsRecord());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getDataKey(), getDataValue(), getStatsRecord());
        }
    }
}
