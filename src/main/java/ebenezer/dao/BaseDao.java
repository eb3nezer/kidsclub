package ebenezer.dao;

import ebenezer.model.ModelObject;

import java.util.Optional;

public interface BaseDao<T extends ModelObject> {
    Optional<T> findById(Long id);
}
