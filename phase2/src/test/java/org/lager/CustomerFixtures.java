package org.lager;

import org.lager.model.Customer;

public class CustomerFixtures {
    private final static long CUSTOMER_1_ID = 100_000_000L;
    private final static String CUSTOMER_1_NAME = "customerOne";
    private final static long CUSTOMER_2_ID = 100_000_001L;
    private final static String CUSTOMER_2_NAME = "customerTwo";

    public static long defaultCustomerId() {
        return CUSTOMER_1_ID;
    }

    public static long anotherCustomerId() {
        return CUSTOMER_2_ID;
    }

    public static String defaultCustomerName() {
        return CUSTOMER_1_NAME;
    }

    public static long incorrectCustomerId() {
        return 1;
    }

    public static long nonExistingCustomerId() {
        return 999_999_999L;
    }

    public static String incorrectCustomerName() {
        return "incorect!§$%&//(";
    }

    public static Customer defaultNewCustomer() {
        return new Customer(CUSTOMER_1_NAME);
    }

    public static Customer defaultCustomer() {
        return new Customer(CUSTOMER_1_ID, CUSTOMER_1_NAME);
    }

    public static Customer anotherCustomer() {
        return new Customer(CUSTOMER_2_ID, CUSTOMER_2_NAME);
    }

    public static Customer defaultCustomerWithName(String newName) {
        return new Customer(CUSTOMER_1_ID, newName);
    }
}
