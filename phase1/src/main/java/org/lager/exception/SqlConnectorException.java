package org.lager.exception;

public class SqlConnectorException extends RuntimeException{

    public SqlConnectorException(String message) {
        super("SQL Connector was not able to create SQL Tables.\n%s".formatted(message));
    }

    public SqlConnectorException(String query, String message) {
        super("SQL Connector failed to execute Query:\n&1s\nMessage:\n%2s".formatted(query, message));
    }
}
