package org.lager.exception;

public class NoSuchOrderException extends Exception {
    public NoSuchOrderException(long id) {
        super("Order does not exist: " + id);
    }
}
