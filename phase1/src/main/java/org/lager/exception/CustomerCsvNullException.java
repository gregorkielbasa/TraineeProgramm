package org.lager.exception;

public class CustomerCsvNullException extends RuntimeException{
    public CustomerCsvNullException() {
        super("Customer CSV Editor cannot save NULL List");
    }
}
