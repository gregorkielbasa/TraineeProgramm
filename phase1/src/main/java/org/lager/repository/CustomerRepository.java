package org.lager.repository;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends Repository<Customer, Long> {
    @Override
    void create(Long number, Customer customer) throws RepositoryException;

    @Override
    Optional<Customer> read(Long number);

    @Override
    void update(Long number, Customer customer) throws RepositoryException;

    @Override
    void delete(Long number) throws RepositoryException;

    long getNextAvailableNumber();
}
