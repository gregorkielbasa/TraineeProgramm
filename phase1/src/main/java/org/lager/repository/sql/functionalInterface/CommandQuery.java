package org.lager.repository.sql.functionalInterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface CommandQuery {

    ResultSet execute(Connection connection) throws SQLException;
}
