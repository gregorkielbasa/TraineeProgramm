package org.lager.exception;

public class UserExistsAlreadyException extends Exception{
    public UserExistsAlreadyException(String login) {
        super("User does exist already:" + login);
    }
}
