package kc.ebenezer.system.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Component
public class UpgradeTask2 implements DataUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(UpgradeTask2.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String getName() {
        return "Upgrade existing teams to set scoring to be true.";
    }

    @Override
    public Integer getVersion() {
        return 2;
    }

    @Override
    public boolean performUpgrade() {
        LOG.info("Setting existing teams to have scoring=true.");
        try {
            Query query = entityManager.createQuery("update StudentTeam studentteams set studentteams.scoring = true");
            int rows = query.executeUpdate();
            LOG.info("Updated " + rows + " teams.");
            return true;
        } catch (Exception e) {
            LOG.error("Failed to update student teams.", e);
            return false;
        }
    }
}
