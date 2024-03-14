package org.lager.model;

import org.lager.exception.CustomerException;

import java.util.Objects;

public class Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long NUMBER_MIN = 100_000_000;
    private static final long NUMBER_MAX = 999_999_999;
    private final long number;
    private String name;

    public Customer(long number, String name) {
        validNumber(number);
        validName(name);

        this.name = name;
        this.number = number;
    }

    private static void validNumber(long number) {
        if (number < NUMBER_MIN || number > NUMBER_MAX)
            throw new CustomerException("Customer's number is invalid: " + number);
    }

    private static void validName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerException("Customer's name is invalid: " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validName(name);
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