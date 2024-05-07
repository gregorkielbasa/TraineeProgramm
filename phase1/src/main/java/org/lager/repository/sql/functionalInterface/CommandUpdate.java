package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.SQLException;

public interface CommandUpdate {

    void execute(Connection connection) throws SQLException;
}
