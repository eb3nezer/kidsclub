package kc.ebenezer.dao;

import kc.ebenezer.model.SystemConfiguration;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Component
public class SystemConfigurationDao extends BaseDaoImpl<SystemConfiguration> {
    @Override
    public Optional<SystemConfiguration> findById(Long id) {
        TypedQuery<SystemConfiguration> query = getEntityManager().createQuery(
                "select systemConfiguration from SystemConfiguration systemConfiguration " +
                        "where systemConfiguration.id = 1", SystemConfiguration.class);
        SystemConfiguration systemConfiguration = null;
        try {
            systemConfiguration = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(systemConfiguration);
    }
}
