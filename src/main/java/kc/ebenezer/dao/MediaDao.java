package kc.ebenezer.dao;

import kc.ebenezer.model.Media;
import kc.ebenezer.model.User;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class MediaDao extends BaseDaoImpl<Media> {
    @Override
    public Optional<Media> findById(Long id) {
        Media media = null;
        try {
            media = getEntityManager().find(Media.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(media);
    }

    public Optional<Media> findByDescriptor(String descriptor) {
        Media media = null;
        try {
            TypedQuery<Media> query = getEntityManager().createQuery("select media from Media media where media.descriptor = :descriptor", Media.class);
            query.setParameter("descriptor", descriptor);
            media = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(media);
    }
}
