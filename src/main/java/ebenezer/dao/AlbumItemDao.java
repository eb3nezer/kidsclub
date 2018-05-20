package ebenezer.dao;

import ebenezer.model.AlbumItem;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import java.util.Optional;

@Component
public class AlbumItemDao extends BaseDaoImpl<AlbumItem> {
    @Override
    public Optional<AlbumItem> findById(Long id) {
        AlbumItem item = null;
        try {
            item = getEntityManager().find(AlbumItem.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(item);
    }
}
