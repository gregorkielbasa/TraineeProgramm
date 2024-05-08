package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSupplierImpl implements ConnectionSupplier {

    private final String postgresqlUrl;
    private final String postgresqlUser;
    private final String postgresqlPassword;

    public ConnectionSupplierImpl(String postgresqlUrl, String postgresqlUser, String postgresqlPassword) {
        this.postgresqlUrl = postgresqlUrl;
        this.postgresqlUser = postgresqlUser;
        this.postgresqlPassword = postgresqlPassword;
    }

    @Override
    public Connection get() throws SQLException {
        return DriverManager
                .getConnection(postgresqlUrl, postgresqlUser, postgresqlPassword);
    }
}
