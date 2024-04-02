package org.lager.repository;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends Repository<Customer, Long> {

    long getNextAvailableNumber();
}
