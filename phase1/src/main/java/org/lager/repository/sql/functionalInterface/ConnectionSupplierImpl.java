package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSupplierImpl implements ConnectionSupplier {

    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    public ConnectionSupplierImpl(String databaseUrl, String databaseUser, String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    @Override
    public Connection get() throws SQLException {
        return DriverManager
                .getConnection(databaseUrl, databaseUser, databasePassword);
    }
}
