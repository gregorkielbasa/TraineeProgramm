package org.lager.exception;

public class ProductIllegalNumberException extends RuntimeException {
    public ProductIllegalNumberException(long number) {
        super("Product's number is invalid: " + number);
    }
}
