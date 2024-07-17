package org.lager.exception;

public class OrderIllegalIdException extends RuntimeException {
    public OrderIllegalIdException(long id) {
        super("Order's ID is invalid: " + id);
    }
}
