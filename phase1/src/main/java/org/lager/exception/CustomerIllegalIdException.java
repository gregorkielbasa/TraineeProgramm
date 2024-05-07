package org.lager.exception;

public class CustomerIllegalIdException extends RuntimeException {
    public CustomerIllegalIdException(long id) {
        super("Customer's ID is invalid: " + id);
    }
}
