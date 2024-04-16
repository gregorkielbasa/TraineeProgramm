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
        long newCustomerNumber = repository.getNextAvailableNumber();
        logger.debug("CustomerService starts to insert new Customer with {} ID and {} name", newCustomerNumber, newCustomerName);
        Customer newCustomer = new Customer(newCustomerNumber, newCustomerName);
        repository.save(newCustomer);
        logger.debug("CustomerService finished to insert new {} Customer", newCustomerNumber);
        return newCustomer;
    }

    public Optional<Customer> search(long customerNumber) {
        return repository.read(customerNumber);
    }

    public void validatePresence(long customerNumber) {
        search(customerNumber)
                .orElseThrow(() -> new NoSuchCustomerException(customerNumber));
    }

    public void delete(long customerNumber) {
        logger.info("CustomerService deletes {} Customer", customerNumber);
        repository.delete(customerNumber);
    }

    public void rename(long customerNumber, String customerNewName) {
        logger.debug("CustomerService tries to rename {} Customer to {}", customerNumber, customerNewName);
        Customer customer = search(customerNumber)
                .orElseThrow(() -> new NoSuchCustomerException(customerNumber));
        customer.setName(customerNewName);
        repository.save(customer);
    }
}