package org.lager.exception;

public class OrderTimeNullException extends RuntimeException {

    public OrderTimeNullException(long id) {
        super("Order's Time Stamp is empty. Order ID: " + id);
    }
}
