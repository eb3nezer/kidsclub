package ebenezer.dao;

import ebenezer.model.Album;
import ebenezer.model.Project;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class AlbumDao extends BaseDaoImpl<Album> {
    @Override
    public Optional<Album> findById(Long id) {
        TypedQuery<Album> query = getEntityManager().createQuery(
                "select album from Album album left join album.project project " +
                        "where album.id = :albumId", Album.class);
        query.setParameter("albumId", id);
        Album album = null;
        try {
            album = query.getSingleResult();
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(album);
    }

    public List<Album> getAlbumsForProject(Long projectId) {
        TypedQuery<Album> query = getEntityManager().createQuery(
                "select album from Album album where album.project.id = :projectId", Album.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }
}
