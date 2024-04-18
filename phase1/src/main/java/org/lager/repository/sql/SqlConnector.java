package org.lager.repository.sql;

import org.lager.exception.SqlConnectorException;

import java.sql.*;

public class SqlConnector {
    private final String url;
    private final String user;
    private final String password;

    public SqlConnector() {
        this.url = "jdbc:postgresql://localhost:5432/shopdb";
        this.user = "postgres";
        this.password = "pass";
    }

    public SqlConnector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void saveToDB(String query) throws SqlConnectorException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlConnectorException(query, e.getMessage());
        }
    }

    public ResultSet loadFromDB(String query) throws SqlConnectorException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            return resultSet;
        } catch (SQLException e) {
            throw new SqlConnectorException(query, e.getMessage());
        }
    }
}
