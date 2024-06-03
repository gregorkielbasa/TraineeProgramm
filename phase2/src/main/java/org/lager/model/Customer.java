package org.lager.model;

import jakarta.persistence.*;
import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;

@Table(name = "CUSTOMERS")
@Entity
public class Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final static Logger logger = LoggerFactory.getLogger(Customer.class);

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CUSTOMER_KEY")
    @SequenceGenerator(name = "CUSTOMER_KEY", initialValue = (int) ID_MIN, allocationSize = 1)
    private long customerId;
    private String customerName;

    public Customer() {
    }

    public Customer(String customerName) {
        this(0, customerName);
        logger.info("New Customer {} has been created.", customerName);
    }

    @PersistenceCreator
    public Customer(long customerId, String customerName) {
        validateId(customerId);
        validateName(customerName);

        this.customerId = customerId;
        this.customerName = customerName;
    }

    private static void validateId(long customerId) {
        if (customerId != 0 && (customerId < ID_MIN || customerId > ID_MAX))
            throw new CustomerIllegalIdException(customerId);
    }

    private static void validateName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerIllegalNameException(name);
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        logger.info("Customer {} with {} customerName is changing its customerName to {}.", this.customerId, this.customerName, customerName);
        validateName(customerName);
        this.customerName = customerName;
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

    @Override
    public String toString() {
        return "Customer{" +
                "ID=" + customerId +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}