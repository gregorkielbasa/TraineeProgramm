package org.lager.exception;

public class NoSuchProductException extends RuntimeException{
    public NoSuchProductException(long number) {
        super("Product does not exist: " + number);
    }
}
