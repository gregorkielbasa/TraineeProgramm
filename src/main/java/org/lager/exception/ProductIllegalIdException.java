package org.lager.exception;

public class ProductIllegalIdException extends RuntimeException {
    public ProductIllegalIdException(long id) {
        super("Product's ID is invalid: " + id);
    }
}
