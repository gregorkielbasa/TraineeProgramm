package org.lager;

import org.lager.model.Product;

public class ProductFixtures {
    private final static long PRODUCT_1_NUMBER = 100_000_000L;
    private final static String PRODUCT_1_NAME = "Product One";
    private final static long PRODUCT_2_NUMBER = 100_000_001L;
    private final static String PRODUCT_2_NAME = "Product Two";

    public static long defaultNumber() {
        return PRODUCT_1_NUMBER;
    }

    public static String defaultName() {
        return PRODUCT_1_NAME;
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

    public static Product defaultProduct() {
        return new Product(PRODUCT_1_NUMBER, PRODUCT_1_NAME);
    }

    public static String defaultProductAsCsvRecord() {
        return PRODUCT_1_NUMBER + "," + PRODUCT_1_NAME;

    }

    public static long anotherNumber() {
        return PRODUCT_2_NUMBER;
    }

    public static Product anotherProduct() {
        return new Product(PRODUCT_2_NUMBER, PRODUCT_2_NAME);
    }

    public static String anotherProductAsCsvRecord() {
        return PRODUCT_2_NUMBER + "," + PRODUCT_2_NAME;
    }

    public static Product productWithName(String newName) {
        return new Product(PRODUCT_1_NUMBER, newName);
    }
}
