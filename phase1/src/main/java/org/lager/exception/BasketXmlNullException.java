package org.lager.exception;

public class BasketXmlNullException extends RuntimeException{
    public BasketXmlNullException() {
        super("Basket XML Editor cannot save NULL List");
    }
}
