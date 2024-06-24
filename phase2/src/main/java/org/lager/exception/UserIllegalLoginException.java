package org.lager.exception;

public class UserIllegalLoginException extends Exception{
    public UserIllegalLoginException(String login) {
        super("User's login is invalid: " + login);
    }
}
