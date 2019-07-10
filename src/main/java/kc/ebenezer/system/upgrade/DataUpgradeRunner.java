package kc.ebenezer.system.upgrade;

import kc.ebenezer.dao.SystemConfigurationDao;
import kc.ebenezer.dao.UpgradeTaskHistoryDao;
import kc.ebenezer.model.SystemConfiguration;
import kc.ebenezer.model.UpgradeTaskHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class DataUpgradeRunner {
    private static final Logger LOG = LoggerFactory.getLogger(DataUpgradeRunner.class);

    @Inject
    private SystemConfigurationDao systemConfigurationDao;
    @Inject
    private UpgradeTaskHistoryDao upgradeTaskHistoryDao;

    @Autowired
    private List<DataUpgradeTask> dataUpgradeTasks;

    private SystemConfiguration getCurrentSystemConfiguration() {
        LOG.info("Getting system configuration.");
        Optional<SystemConfiguration> systemConfiguration = systemConfigurationDao.findById(1L);
        if (!systemConfiguration.isPresent()) {
            LOG.info("System was not previously configured.");
            SystemConfiguration newSystemConfiguration = new SystemConfiguration();
            newSystemConfiguration.setVersion(0);
            systemConfigurationDao.create(newSystemConfiguration);
            LOG.info("Updated system to version 0.");
            systemConfiguration = systemConfigurationDao.findById(1L);
            if (!systemConfiguration.isPresent()) {
                LOG.error("Failed to insert new system configuration!");
            }
        }

        return systemConfiguration.orElse(null);
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SystemConfiguration systemConfiguration = getCurrentSystemConfiguration();
        LOG.info("System is running version " + systemConfiguration.getVersion());
        dataUpgradeTasks.sort(new DataUpgradeTaskComparator());
        boolean continueRunning = true;
        for (DataUpgradeTask dataUpgradeTask : dataUpgradeTasks) {
            if (continueRunning && dataUpgradeTask.getVersion() > systemConfiguration.getVersion()) {
                LOG.info("Data upgrade task \"" + dataUpgradeTask.getName() + "\" upgrades to version " + dataUpgradeTask.getVersion() +
                    " and has not been run. Running it now.");
                continueRunning = dataUpgradeTask.performUpgrade();
                UpgradeTaskHistory upgradeTaskHistory = new UpgradeTaskHistory(
                    dataUpgradeTask.getVersion(),
                    dataUpgradeTask.getName(),
                    continueRunning,
                    System.currentTimeMillis());
                upgradeTaskHistoryDao.create(upgradeTaskHistory);

                if (continueRunning) {
                    LOG.info("Data upgrade task \"" + dataUpgradeTask.getName() + "\" succeeded.");
                    systemConfiguration.setVersion(dataUpgradeTask.getVersion());
                    systemConfigurationDao.flush();
                    LOG.info("System is now running version " + systemConfiguration.getVersion());
                } else {
                    LOG.error("Data upgrade task \"" + dataUpgradeTask.getName() + "\" failed.");
                }
            }
        }
    }

    public class DataUpgradeTaskComparator implements Comparator<DataUpgradeTask> {
        @Override
        public int compare(DataUpgradeTask o1, DataUpgradeTask o2) {
            return o1.getVersion() - o2.getVersion();
        }
    }
}
