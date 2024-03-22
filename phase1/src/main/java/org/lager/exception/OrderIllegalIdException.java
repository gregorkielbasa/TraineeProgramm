package org.lager.exception;

public class OrderIllegalIdException extends RuntimeException {
    public OrderIllegalIdException(long id) {
        super("Order's id is invalid: " + id);
    }
}
