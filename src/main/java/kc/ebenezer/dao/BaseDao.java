package kc.ebenezer.dao;

import kc.ebenezer.model.ModelObject;

import java.util.Optional;

public interface BaseDao<T extends ModelObject> {
    Optional<T> findById(Long id);
}
