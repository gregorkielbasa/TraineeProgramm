package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.function.Supplier;

@DisplayName("SQL Connector")
class SqlConnectorTest implements WithAssertions {
    private static final String url = "jdbc:postgresql://localhost:5432/testdb";
    private static final String user = "postgres";
    private static final String password = "pass";
    private Supplier<Connection> connectionSupplier; //connectionSupplier = () -> DriverManager.getConnection(url, user, password);
    private  SqlConnector connector; // new SqlConnector(connectionSupplier);

}