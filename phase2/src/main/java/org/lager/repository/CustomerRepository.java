package org.lager.repository;

import org.lager.model.Customer;

public interface CustomerRepository extends Repository<Customer, Long> {

    long getNextAvailableId();
}
