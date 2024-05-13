package org.lager.exception;

public class OrderTimeNullException extends RuntimeException {

    public OrderTimeNullException(long id) {
        super("The order's timestamp is null, order id: %s".formatted(id));
    }
}
