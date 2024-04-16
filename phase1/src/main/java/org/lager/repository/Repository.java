package org.lager.repository;

import org.lager.exception.RepositoryException;

import java.util.Optional;

public interface Repository <T, ID> {

    void save (T entity) throws RepositoryException;

    Optional<T> read (ID id);

    void delete (ID id) throws RepositoryException;
}
