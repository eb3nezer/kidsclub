package kc.ebenezer.dao;

import kc.ebenezer.model.Image;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Component
public class ImageDao extends BaseDaoImpl<Image> {
    @Override
    public Optional<Image> findById(Long id) {
        TypedQuery<Image> query = getEntityManager().createQuery(
                "select image from Image image where image.id = :imageId", Image.class);
        query.setParameter("imageId", id);
        Image image = null;
        try {
            image = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(image);
    }
}
