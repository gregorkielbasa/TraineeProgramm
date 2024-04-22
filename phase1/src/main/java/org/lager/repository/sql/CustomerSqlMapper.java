package org.lager.repository.sql;

import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
import org.lager.repository.xml.BasketXmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class CustomerSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public ResultSetDecoder<Optional<Customer>> getResultSetDecoder() {
        return resultSet -> {
            try {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");

                    Customer newCustomer = new Customer(id, name);
                    return Optional.of(newCustomer);
                }
            } catch (SQLException e) {
                logger.warn("Customer SQL Mapper was not able to decode Customer");
            }
            return Optional.empty();
        };
    }

    public CommandUpdate getInitialCommand() {
        return connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS customers (
                        id bigint PRIMARY KEY,
                        name character varying(24) NOT NULL
                        );""");
            }
        };
    }

    public CommandQuery getCustomerWithHighestIdCommand() {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;")) {
                return statement.executeQuery();
            }
        };
    }

    public CommandQuery getReadCommand(Long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM Customers WHERE id=?;")) {
                statement.setLong(1, id);
                return statement.executeQuery();
            }
        };
    }

    public CommandUpdate getDeleteCommand(Long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM customers WHERE id=?;")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        };
    }

    public CommandUpdate getInsertCommand(Customer customer) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO Customers VALUES (?, ?);")) {
                statement.setLong(1, customer.getId());
                statement.setString(2, customer.getName());
                statement.executeUpdate();
            }
        };
    }

    public CommandUpdate getUpdateNameCommand(Customer customer) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE Customers SET name=? WHERE id=?;")) {
                statement.setString(1, customer.getName());
                statement.setLong(2, customer.getId());
                statement.executeUpdate();
            }
        };
    }
}
