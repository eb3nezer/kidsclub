package ebenezer.dao;

import ebenezer.model.ProjectDocument;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class ProjectDocumentDao extends BaseDaoImpl<ProjectDocument> {
    @Override
    public Optional<ProjectDocument> findById(Long id) {
        ProjectDocument item = null;
        try {
            item = getEntityManager().find(ProjectDocument.class, id);
        } catch (NoResultException e) {
        }

        return Optional.ofNullable(item);
    }

    public List<ProjectDocument> getDocumentsForProject(Long projectId) {
        TypedQuery<ProjectDocument> query = getEntityManager().createQuery(
                "select document from ProjectDocument document where document.project.id = :projectId " +
                "order by document.updated desc",
                ProjectDocument.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }
}
