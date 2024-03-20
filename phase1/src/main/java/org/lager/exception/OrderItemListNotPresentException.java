package org.lager.exception;

public class OrderItemListNotPresentException extends RuntimeException{
    public OrderItemListNotPresentException(long ID) {
        super("Order's List of Items is empty. Order ID: " + ID);
    }
}
