package org.lager.exception;

public class NoSuchCustomerException extends RuntimeException{
    public NoSuchCustomerException(long customer) {
        super("Customer does not exist: " + customer);
    }
}
