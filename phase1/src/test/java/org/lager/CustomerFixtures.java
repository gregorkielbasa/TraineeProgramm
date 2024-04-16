package org.lager;

import org.lager.model.Customer;

public class CustomerFixtures {
    private final static long CUSTOMER_1_NUMBER = 100_000_000L;
    private final static String CUSTOMER_1_NAME = "customerOne";
    private final static long CUSTOMER_2_NUMBER = 100_000_001L;
    private final static String CUSTOMER_2_NAME = "customerTwo";

    public static long defaultNumber() {
        return CUSTOMER_1_NUMBER;
    }

    public static String defaultName() {
        return CUSTOMER_1_NAME;
    }

    public static long incorrectNumber() {
        return 1;
    }

    public static long nonExistingNumber() {
        return 999_999_999L;
    }

    public static String incorrectName() {
        return "incorect!§$%&//(";
    }

    public static Customer defaultCustomer() {
        return new Customer(CUSTOMER_1_NUMBER, CUSTOMER_1_NAME);
    }

    public static String defaultCustomerAsCsvRecord() {
        return CUSTOMER_1_NUMBER + "," + CUSTOMER_1_NAME;

    }    public static long anotherNumber() {
        return CUSTOMER_2_NUMBER;
    }

    public static Customer anotherCustomer() {
        return new Customer(CUSTOMER_2_NUMBER, CUSTOMER_2_NAME);
    }

    public static String anotherCustomerAsCsvRecord() {
        return CUSTOMER_2_NUMBER + "," + CUSTOMER_2_NAME;
    }

    public static Customer customerWithName(String newName) {
        return new Customer(CUSTOMER_1_NUMBER, newName);
    }
}
