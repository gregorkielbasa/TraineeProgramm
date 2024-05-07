package org.lager.service;

import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CustomerService {
    private final CustomerRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer create(String newCustomerName) {
        long newCustomerId = repository.getNextAvailableId();
        logger.debug("CustomerService starts to insert new Customer with {} ID and {} name", newCustomerId, newCustomerName);
        Customer newCustomer = new Customer(newCustomerId, newCustomerName);
        repository.save(newCustomer);
        logger.debug("CustomerService finished to insert new {} Customer", newCustomerId);
        return newCustomer;
    }

    public Optional<Customer> search(long customerId) {
        return repository.read(customerId);
    }

    public void validatePresence(long customerId) {
        search(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
    }

    public void delete(long customerId) {
        logger.info("CustomerService deletes {} Customer", customerId);
        repository.delete(customerId);
    }

    public void rename(long customerId, String customerNewName) {
        logger.debug("CustomerService tries to rename {} Customer to {}", customerId, customerNewName);
        Customer customer = search(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
        customer.setName(customerNewName);
        repository.save(customer);
    }
}