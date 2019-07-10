package kc.ebenezer.system.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InitialUpgradeTask implements DataUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(InitialUpgradeTask.class);

    @Override
    public String getName() {
        return "Initial upgrade task. Upgrades to version 1 to initialise the upgrade task system.";
    }

    @Override
    public Integer getVersion() {
        return 1;
    }

    @Override
    public boolean performUpgrade() {
        LOG.info("Performing dummy upgrade...");
        return true;
    }
}
