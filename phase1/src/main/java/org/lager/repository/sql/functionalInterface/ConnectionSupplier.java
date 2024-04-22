package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public interface ConnectionSupplier {

    public Connection get() throws SQLException;
}
