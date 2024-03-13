package org.lager.service;

import org.lager.model.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private static long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;

    public CustomerService() {
        this.customers = new HashMap<>();
    }

    public List<Customer> getAll () {
        return new ArrayList<>(customers.values());
    }

    public Customer create(String newCustomerName) {
        Customer newCustomer = new Customer(newCustomerNumber, newCustomerName);
        customers.put(newCustomerNumber, newCustomer);
        newCustomerNumber++;
        return newCustomer;
    }

    public Customer search(long customerNumber) {
        return customers.get(customerNumber);
    }

    public Customer remove(long customerNumber) {
        return customers.remove(customerNumber);
    }

    public Customer rename(long customerNumber, String customerNewName) {
        Customer customer = search(customerNumber);
        if (null != customer)
            customer.setName(customerNewName);
        return customer;
    }
}
