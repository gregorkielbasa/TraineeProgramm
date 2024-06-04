package org.lager.service;

import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerService {
    private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository repository;
    private final BasketRepository basketRepository;

    public CustomerService(CustomerRepository repository, BasketRepository basketRepository) {
        this.repository = repository;
        this.basketRepository = basketRepository;
    }

    public Customer create(String newCustomerName) {
        logger.debug("CustomerService starts to insert new Customer with {} name", newCustomerName);
        Customer newCustomer = new Customer(newCustomerName);
        newCustomer = repository.save(newCustomer);
        logger.debug("CustomerService finished to insert new {} Customer", newCustomer.getCustomerId());
        return newCustomer;
    }

    public Optional<Customer> search(long customerId) {
        return repository.findById(customerId);
    }

    public void validatePresence(long customerId) {
        search(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
    }

    @Transactional
    public void delete(long customerId) {
        logger.info("CustomerService deletes {} Customer", customerId);
        basketRepository.deleteByCustomerId(customerId);
        repository.deleteById(customerId);
    }

    public void rename(long customerId, String customerNewName) {
        logger.debug("CustomerService tries to rename {} Customer to {}", customerId, customerNewName);
        Customer customer = search(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
        customer.setCustomerName(customerNewName);
        repository.save(customer);
    }
}