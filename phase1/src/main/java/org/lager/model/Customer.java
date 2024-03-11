package org.lager.model;

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

    }

    private static void validNumber(long number) {

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
}
