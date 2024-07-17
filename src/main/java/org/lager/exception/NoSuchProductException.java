package org.lager.exception;

public class NoSuchProductException extends RuntimeException {
    public NoSuchProductException(long id) {
        super("Product does not exist: " + id);
    }
}
