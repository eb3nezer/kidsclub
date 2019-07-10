package kc.ebenezer.system.upgrade;

public interface DataUpgradeTask {
    /**
     * Get the name of the upgrade task.
     * @return The name of the upgrade task.
     */
    String getName();

    /**
     * Get the version that this task upgrades the DB to. Tasks that return IDs less than the current version
     * will get run in order.
     * @return The version that this task upgrades the DB to.
     */
    Integer getVersion();

    /**
     * Perform the data upgrade.
     * @return Returns true if the task succeeded, or false otherwise.
     */
    boolean performUpgrade();
}
