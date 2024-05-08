package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {

    public Connection get() throws SQLException;
}
