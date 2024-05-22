package org.lager.exception;

public class OrderItemSetNotPresentException extends RuntimeException {
    public OrderItemSetNotPresentException(long id) {
        super("Order's List of Items is empty. Order ID: " + id);
    }
}
