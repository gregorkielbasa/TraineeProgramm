package org.lager.repository;

import org.lager.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    default List<Long> getAllIds() {
        return findAll().stream()
                .map(Customer::getCustomerId)
                .toList();
    }
}
