package org.lager.service;

import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.model.dto.CustomerDto;
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

    public List<Long> getAllIds() {
        return repository.getAllIds();
    }

    public CustomerDto create(String newCustomerName) {
        logger.debug("CustomerService starts to insert new Customer with {} name", newCustomerName);
        Customer newCustomer = new Customer(newCustomerName);
        newCustomer = repository.save(newCustomer);
        logger.debug("CustomerService finished to insert new {} Customer", newCustomer.getCustomerId());
        return new CustomerDto(newCustomer);
    }

    private Optional<Customer> find(long customerId) {
        return repository.findById(customerId);
    }

    public CustomerDto get(long customerId) {
        return find(customerId)
                .map(CustomerDto::new)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
    }

    public void validatePresence(long customerId) {
        find(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
    }

    @Transactional
    public void delete(long customerId) {
        logger.info("CustomerService deletes {} Customer", customerId);
        basketRepository.deleteByCustomerId(customerId);
        repository.deleteById(customerId);
    }

    public CustomerDto rename(long customerId, String customerNewName) {
        logger.debug("CustomerService tries to rename {} Customer to {}", customerId, customerNewName);
        Customer customer = find(customerId)
                .orElseThrow(() -> new NoSuchCustomerException(customerId));
        customer.setCustomerName(customerNewName);
        return new CustomerDto(repository.save(customer));
    }
}