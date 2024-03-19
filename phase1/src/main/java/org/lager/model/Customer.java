package org.lager.model;

import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.CustomerIllegalNumberException;

import java.util.Objects;

public class Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long NUMBER_MIN = 100_000_000;
    private static final long NUMBER_MAX = 999_999_999;
    private final long number;
    private String name;

    public Customer(long number, String name) {
        validateNumber(number);
        validateName(name);

        this.name = name;
        this.number = number;
    }

    private static void validateNumber(long number) {
        if (number < NUMBER_MIN || number > NUMBER_MAX)
            throw new CustomerIllegalNumberException(number);
    }

    private static void validateName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerIllegalNameException(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return number == customer.number && Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
}