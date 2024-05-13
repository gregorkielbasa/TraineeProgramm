package org.lager.exception;

public class SqlConnectionException extends RuntimeException{
    public SqlConnectionException(String message) {
        super("SQL Connection has been corrupted:\n" + message);
    }
}
