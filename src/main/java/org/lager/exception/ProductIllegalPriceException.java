package org.lager.exception;

public class ProductIllegalPriceException extends RuntimeException {
    public ProductIllegalPriceException(double productPrice) {
        super("Product's price is invalid: " + productPrice);
    }
}
