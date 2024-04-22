package org.lager.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SqlConnectorException extends RuntimeException{
    public SqlConnectorException(String message, String sqlError) {
        super("SQL Connector encountered Error:\n%s\n%s"
                .formatted(message, sqlError));
    }
}
