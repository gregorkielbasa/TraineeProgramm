package org.lager;

import org.lager.model.Product;

public class ProductFixtures {
    private final static long PRODUCT_1_ID = 100_000_000L;
    private final static String PRODUCT_1_NAME = "Product One";
    private final static long PRODUCT_2_ID = 100_000_001L;
    private final static String PRODUCT_2_NAME = "Product Two";

    public static long defaultProductId() {
        return PRODUCT_1_ID;
    }

    public static String defaultProductName() {
        return PRODUCT_1_NAME;
    }

    public static long incorrectProductId() {
        return 1;
    }

    public static long nonExistingProductId() {
        return 999_999_999L;
    }

    public static String incorrectProductName() {
        return "incorect!§$%&//(";
    }

    public static Product defaultNewProduct() {
        return new Product(PRODUCT_1_NAME);
    }

    public static Product defaultProduct() {
        return new Product(PRODUCT_1_ID, PRODUCT_1_NAME);
    }

    public static long anotherProductId() {
        return PRODUCT_2_ID;
    }

    public static Product anotherProduct() {
        return new Product(PRODUCT_2_ID, PRODUCT_2_NAME);
    }

    public static Product defaultProductWithName(String newName) {
        return new Product(PRODUCT_1_ID, newName);
    }
}
