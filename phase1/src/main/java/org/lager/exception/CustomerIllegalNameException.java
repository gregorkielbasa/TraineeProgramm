package org.lager.exception;

public class CustomerIllegalNameException extends RuntimeException{
    public CustomerIllegalNameException(String name) {
        super("Customer's name is invalid: " + name);
    }
}
