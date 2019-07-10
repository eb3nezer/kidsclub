package kc.ebenezer.dao;

import kc.ebenezer.model.UpgradeTaskHistory;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class UpgradeTaskHistoryDao extends BaseDaoImpl<UpgradeTaskHistory> {
    @Override
    public Optional<UpgradeTaskHistory> findById(Long id) {
        TypedQuery<UpgradeTaskHistory> query = getEntityManager().createQuery(
                "select upgradeTaskHistory from UpgradeTaskHistory upgradeTaskHistory " +
                        "where upgradeTaskHistory.id = :id", UpgradeTaskHistory.class);
        query.setParameter("id", id);
        UpgradeTaskHistory upgradeTaskHistory = null;
        try {
            upgradeTaskHistory = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(upgradeTaskHistory);
    }

    public List<UpgradeTaskHistory> getUpgradeTasks() {
        TypedQuery<UpgradeTaskHistory> query = getEntityManager().createQuery(
            "select upgradeTaskHistory from UpgradeTaskHistory upgradeTaskHistory " +
            "order by upgradeTaskHistory.runDate", UpgradeTaskHistory.class);
        return query.getResultList();
    }
}
