package org.lager.exception;

public class CustomerIllegalNumberException extends RuntimeException {
    public CustomerIllegalNumberException(long number) {
        super("Customer's number is invalid: " + number);
    }
}
