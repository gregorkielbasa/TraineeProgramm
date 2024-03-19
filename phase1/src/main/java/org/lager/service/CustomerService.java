package org.lager.service;

import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;

import java.util.*;

public class CustomerService {
    private long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;

    public CustomerService() {
        this.customers = new HashMap<>();
    }

    public List<Customer> getAll() {
        return new ArrayList<>(customers.values());
    }

    public Customer create(String newCustomerName) {
        Customer newCustomer = new Customer(newCustomerNumber, newCustomerName);
        customers.put(newCustomerNumber, newCustomer);
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
        customers.remove(customerNumber);
    }

    public void rename(long customerNumber, String customerNewName) {
        Customer customer = search(customerNumber)
                .orElseThrow(() -> new NoSuchCustomerException(customerNumber));
        customer.setName(customerNewName);
    }
}