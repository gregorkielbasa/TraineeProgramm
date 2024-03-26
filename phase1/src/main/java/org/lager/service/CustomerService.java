package org.lager.service;

import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.repository.CustomerCsvEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CustomerService {
    private long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;
    private final CustomerCsvEditor csvEditor;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerCsvEditor csvEditor) {
        this.csvEditor = csvEditor;
        this.customers = new HashMap<>();
        loadFromFile();
    }

    public List<Customer> getAll() {
        return new ArrayList<>(customers.values());
    }

    public Customer create(String newCustomerName) {
        logger.debug("CustomerService starts to insert new Customer with {} ID and {} name", newCustomerNumber, newCustomerName);
        Customer newCustomer = new Customer(newCustomerNumber, newCustomerName);
        customers.put(newCustomerNumber, newCustomer);
        saveToFile();
        logger.debug("CustomerService finished to insert new {} Customer", newCustomerNumber);
        newCustomerNumber++;
        return newCustomer;
    }

    public Optional<Customer> search(long customerNumber) {
        return Optional.ofNullable(customers.get(customerNumber));
    }

    public boolean validatePresence(long number) {
        search(number)
                .orElseThrow(() -> new NoSuchCustomerException(number));
        return true;
    }

    public void remove(long customerNumber) {
        logger.info("CustomerService removes {} Customer", customerNumber);
        customers.remove(customerNumber);
        saveToFile();
    }

    public void rename(long customerNumber, String customerNewName) {
        logger.debug("CustomerService tries to rename {} Customer to {}", customerNumber, customerNewName);
        Customer customer = search(customerNumber)
                .orElseThrow(() -> new NoSuchCustomerException(customerNumber));
        customer.setName(customerNewName);
        saveToFile();
    }

    private void saveToFile() {
        try {
            csvEditor.saveToFile(customers.values().stream().toList());
            logger.info("CustomerService saved its state to CSV File");
        } catch (IOException e) {
            logger.error("CustomerService was not able to save CSV File");
        }
    }

    private void loadFromFile() {
        try {
            csvEditor.loadFromFile().forEach(customer -> customers.put(customer.getNumber(), customer));
            logger.info("CustomerService loaded its state from CSV File");
        } catch (IOException e) {
            logger.warn("CustomerService was not able to load CSV File");
        }
    }
}