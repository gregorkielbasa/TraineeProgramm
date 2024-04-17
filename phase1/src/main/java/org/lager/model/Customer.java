package org.lager.model;

import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.CustomerIllegalIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class  Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final long id;
    private String name;

    private final static Logger logger = LoggerFactory.getLogger(Customer.class);

    public Customer(long id, String name) {
        validateId(id);
        validateName(name);

        this.name = name;
        this.id = id;

        logger.info("New Customer {} has been created.", id);
    }

    private static void validateId(long id) {
        if (id < ID_MIN || id > ID_MAX)
            throw new CustomerIllegalIdException(id);
    }

    private static void validateName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerIllegalNameException(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("Customer {} with {} name is changing its name to {}.", this.id, this.name, name);
        validateName(name);
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id && Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}