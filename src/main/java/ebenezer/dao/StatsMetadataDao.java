package ebenezer.dao;

import ebenezer.model.StatsMetadata;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import java.util.Optional;

@Component
public class StatsMetadataDao extends BaseDaoImpl<StatsMetadata> {
    @Override
    public Optional<StatsMetadata> findById(Long id) {
        StatsMetadata statsRecord = null;
        try {
            statsRecord = getEntityManager().find(StatsMetadata.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(statsRecord);
    }
}
