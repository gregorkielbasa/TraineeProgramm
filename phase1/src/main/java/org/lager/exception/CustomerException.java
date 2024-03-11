package org.lager.exception;

public class CustomerException extends RuntimeException{
    public CustomerException(String message) {
        super(message);
    }
}