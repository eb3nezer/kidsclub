package kc.ebenezer.dao;

import kc.ebenezer.model.ImageCollection;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Component
public class ImageCollectionDao extends BaseDaoImpl<ImageCollection> {
    @Override
    public Optional<ImageCollection> findById(Long id) {
        TypedQuery<ImageCollection> query = getEntityManager().createQuery(
                "select imagecol from ImageCollection imagecol where imagecol.id = :imageCollectionId", ImageCollection.class);
        query.setParameter("imageCollectionId", id);
        ImageCollection imageCollection = null;
        try {
            imageCollection = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(imageCollection);
    }
}
