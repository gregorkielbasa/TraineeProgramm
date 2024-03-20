package org.lager.exception;

public class OrderIllegalIDException extends RuntimeException{
    public OrderIllegalIDException(long ID) {
        super("Order's ID is invalid: " + ID);
    }
}
