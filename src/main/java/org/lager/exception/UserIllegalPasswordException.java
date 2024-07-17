package org.lager.exception;

public class UserIllegalPasswordException extends Exception{
    public UserIllegalPasswordException(String password) {
        super("User's password is invalid: " + password);
    }
}
