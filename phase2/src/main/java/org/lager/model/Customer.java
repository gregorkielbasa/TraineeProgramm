package org.lager.model;

import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.CustomerIllegalIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("CUSTOMERS")
public class Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    @Id
    private final long customerId;
    private String customerName;
    @Transient
    private final static Logger logger = LoggerFactory.getLogger(Customer.class);

    public Customer(String customerName) {
        this(0, customerName);
    }

    @PersistenceCreator
    public Customer(long customerId, String customerName) {
        validateId(customerId);
        validateName(customerName);

        this.customerId = customerId;
        this.customerName = customerName;

        logger.info("New Customer {} has been created.", customerId);
    }

    private static void validateId(long customerId) {
        if (customerId != 0 && (customerId < ID_MIN || customerId > ID_MAX))
            throw new CustomerIllegalIdException(customerId);
    }

    private static void validateName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerIllegalNameException(name);
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        logger.info("Customer {} with {} customerName is changing its customerName to {}.", this.customerId, this.customerName, customerName);
        validateName(customerName);
        this.customerName = customerName;
    }

    public long getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "ID=" + customerId +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId && Objects.equals(customerName, customer.customerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerName, customerId);
    }
}