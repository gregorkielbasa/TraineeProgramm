package org.lager.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SqlConnectorException extends RuntimeException{

    public SqlConnectorException(String message) {
        super("SQL Connector was not able to create SQL Tables.\n%s"
                .formatted(message));
    }

    public SqlConnectorException(String message, String query) {
        super("SQL Connector failed to execute Query:\n%s\nMessage:\n%s"
                .formatted(query, message));
    }

    public SqlConnectorException(String message, String... queries) {
        super("SQL Connector failed to execute Query:\n%s\nMessage:\n%s"
                .formatted(String.join("\n", queries), message));
    }
}
