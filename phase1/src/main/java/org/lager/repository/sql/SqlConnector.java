package org.lager.repository.sql;

import org.lager.exception.SqlConnectorException;

import java.sql.*;

public class SqlConnector {
    private final String url = "jdbc:postgresql://localhost:5432/shopdb";
    private final String user = "postgres";
    private final String password = "pass";

    public SqlConnector() {
        createTablesIfNotExist();
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

    private void createTablesIfNotExist() throws SqlConnectorException {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                    id bigint PRIMARY KEY,
                    name character varying(24) NOT NULL
                    );""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                    id bigint PRIMARY KEY,
                    name character varying(24) NOT NULL
                    );""");
        } catch (SQLException e) {
            throw new SqlConnectorException(e.getMessage());
        }
    }
}
