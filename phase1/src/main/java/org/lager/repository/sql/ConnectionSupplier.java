package org.lager.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSupplier {

    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    public ConnectionSupplier(String databaseUrl, String databaseUser, String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public Connection get() throws SQLException {
        Connection connection = DriverManager
                .getConnection(databaseUrl, databaseUser, databasePassword);
        connection.setAutoCommit(false);
        return connection;
    }
}
