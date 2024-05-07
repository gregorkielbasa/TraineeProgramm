package org.lager.exception;

public class SqlCommandException extends RuntimeException{
    public SqlCommandException(String message) {
        super("SQL Query failed to execute.\n" + message);
    }
}
