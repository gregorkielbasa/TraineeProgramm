package org.lager.service;

import org.lager.model.Customer;

public class CustomerFixtures {
    private final static long CUSTOMER_1_NUMBER = 100_000_000L;
    private final static String CUSTOMER_1_NAME = "customerOne";

    public static long defaultNumber() {
        return CUSTOMER_1_NUMBER;
    }

    public static String defaultName() {
        return CUSTOMER_1_NAME;
    }

    public static long incorrectNumber() {
        return 1;
    }

    public static String incorrectName() {
        return "incorect!§$%&//(";
    }

    public static Customer defaultCustomer() {
        return new Customer(CUSTOMER_1_NUMBER, CUSTOMER_1_NAME);
    }

    public static Customer customerWithName(String newName) {
        return new Customer(CUSTOMER_1_NUMBER, newName);
    }
}
