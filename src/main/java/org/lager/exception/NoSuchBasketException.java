package org.lager.exception;

public class NoSuchBasketException extends RuntimeException{
    public NoSuchBasketException(long basket) {
        super("Basket does not exist: " + basket);
    }
}
