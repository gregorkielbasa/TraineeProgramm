package org.lager.model.dto;

import org.lager.model.Customer;

public record CustomerDto(long customerId, String customerName) {

    public CustomerDto(Customer customer) {
        this(customer.getCustomerId(), customer.getCustomerName());
    }
}
