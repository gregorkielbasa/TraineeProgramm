package org.lager;

import org.lager.model.Customer;

public class CustomerFixtures {
    private final static long CUSTOMER_1_ID = 100_000_000L;
    private final static String CUSTOMER_1_NAME = "customerOne";
    private final static long CUSTOMER_2_ID = 100_000_001L;
    private final static String CUSTOMER_2_NAME = "customerTwo";

    public static long defaultId() {
        return CUSTOMER_1_ID;
    }

    public static long anotherId() {
        return CUSTOMER_2_ID;
    }

    public static String defaultName() {
        return CUSTOMER_1_NAME;
    }

    public static long incorrectId() {
        return 1;
    }

    public static long nonExistingId() {
        return 999_999_999L;
    }

    public static String incorrectName() {
        return "incorect!§$%&//(";
    }

    public static Customer defaultCustomer() {
        return new Customer(CUSTOMER_1_ID, CUSTOMER_1_NAME);
    }

    public static String defaultCustomerAsCsvRecord() {
        return CUSTOMER_1_ID + "," + CUSTOMER_1_NAME;

    }

    public static Customer anotherCustomer() {
        return new Customer(CUSTOMER_2_ID, CUSTOMER_2_NAME);
    }

    public static String anotherCustomerAsCsvRecord() {
        return CUSTOMER_2_ID + "," + CUSTOMER_2_NAME;
    }

    public static Customer customerWithName(String newName) {
        return new Customer(CUSTOMER_1_ID, newName);
    }
}
