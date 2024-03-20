package org.lager.exception;

public class ProductIllegalNameException extends RuntimeException{
    public ProductIllegalNameException(String name) {
        super("Product's name is invalid: " + name);
    }
}
