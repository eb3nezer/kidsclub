package kc.ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "upgradetaskhistory")
@SequenceGenerator(initialValue = 1, name = "upgradehistorygen", sequenceName = "upgradehistory_sequence")
public class UpgradeTaskHistory extends ModelObject {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upgradehistorygen")
    private Long id;

    @Column(name = "taskid")
    @NotNull
    private Integer taskId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "succeeded")
    @NotNull
    private boolean succeeded;

    @Column(name = "run_date")
    @NotNull
    private Long runDate;

    public UpgradeTaskHistory() {
    }

    public UpgradeTaskHistory(@NotNull Integer taskId, @NotNull String name, @NotNull boolean succeeded, @NotNull Long runDate) {
        this.taskId = taskId;
        this.name = name;
        this.succeeded = succeeded;
        this.runDate = runDate;
    }

    public Long getId() {
        return id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public Long getRunDate() {
        return runDate;
    }

    public void setRunDate(Long runDate) {
        this.runDate = runDate;
    }
}
