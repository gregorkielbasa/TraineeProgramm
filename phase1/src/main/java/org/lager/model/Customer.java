package org.lager.model;

import org.lager.exception.CustomerException;

import java.util.Objects;

public class Customer {
    private String name;
    private long number;
    private final Basket basket;

    public Customer(String name, long number) {
        validName(name);
        validNumber(number);

        this.name = name;
        this.number = number;
        this.basket = new Basket();
    }

    private static void validName(String name) {
        if (name == null || !name.matches("^[a-z]{3,16}$"))
            throw new CustomerException("Customer's name is invalid");
    }

    private static void validNumber(long number) {
        if (number<100_000_000 || number>999_999_999)
            throw new CustomerException("Customer's number is invalid");
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

    public void setNumber(long number) {
        validNumber(number);
        this.number = number;
    }

    public Basket getBasket() {
        return basket;
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