package org.lager.repository;

import org.lager.exception.RepositoryException;

import java.util.Optional;

public interface Repository <T, ID> {

    void create (ID id, T entity) throws RepositoryException;

    Optional<T> read (ID id);

    void update (ID id, T entity) throws RepositoryException;

    void delete (ID id) throws RepositoryException;
}
