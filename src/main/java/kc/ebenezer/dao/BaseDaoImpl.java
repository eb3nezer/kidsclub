package kc.ebenezer.dao;

import kc.ebenezer.model.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public abstract class BaseDaoImpl<T extends ModelObject> implements BaseDao {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public BaseDaoImpl() {
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional
    public T create(T newObject) {
        try {
            entityManager.persist(newObject);
        } catch (Exception e) {
            LOG.error("Failed to persist new object of type " + newObject.getClass().getName(), e);
            return null;
        }
        return newObject;
    }

    @Transactional
    public T merge(T newObject) {
        try {
            newObject = entityManager.merge(newObject);
        } catch (Exception e) {
            LOG.error("Failed to persist new object of type " + newObject.getClass().getName(), e);
            return null;
        }
        return newObject;
    }

    @Transactional
    public void delete(T existingObject) {
        entityManager.remove(existingObject);
    }

    public void flush() {
        entityManager.flush();
    }

    public void detach(T objectToDetach) {
        entityManager.detach(objectToDetach);
    }
}
