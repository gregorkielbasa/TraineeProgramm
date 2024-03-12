package org.lager.service;

import org.lager.model.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerService {

    private Map<Long, Customer> customers;
    private static long newCustomerNumber = 100_000_000;

    public CustomerService() {
        this.customers = new HashMap<>();
    }

    public List<Customer> getAll () {
        return new ArrayList<>(customers.values());
    }

    public Customer create(String newCustomerName) {
        Customer newCustomer = new Customer(newCustomerName, newCustomerNumber);
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
        if (null != customers)
            customer.setName(customerNewName);
        return search(customerNumber);
    }
}
