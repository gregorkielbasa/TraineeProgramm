package org.lager.repository.sql;

import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class CustomerSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(CustomerSqlMapper.class);

    public SqlDecoder<Optional<Customer>> getResultSetDecoder() {
        return resultSet -> {
            try {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");

                    Customer newCustomer = new Customer(id, name);
                    return Optional.of(newCustomer);
                }
            } catch (SQLException e) {
                logger.warn("Customer SQL Mapper was not able to decode Customer" + e.getMessage());
            } catch (CustomerIllegalIdException | CustomerIllegalNameException e) {
                logger.warn("Customer SQL Mapper was not able to create a new Customer");
            }
            return Optional.empty();
        };
    }

    public SqlProcedure getInitialCommand() {
        return connection -> {
            Statement statement = connection.createStatement();
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS customers (
                        id bigint PRIMARY KEY,
                        name character varying(16) NOT NULL
                        );""");
        };
    }

    public SqlFunction getCustomerWithHighestIdCommand() {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM customers ORDER BY id DESC LIMIT 1;");
                return statement.executeQuery();
        };
    }

    public SqlFunction getReadCommand(Long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM customers WHERE id=?;");
                statement.setLong(1, id);
                return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteCommand(Long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM customers WHERE id=?;");
                statement.setLong(1, id);
                statement.executeUpdate();
        };
    }

    public SqlProcedure getInsertCommand(Customer customer) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO customers VALUES (?, ?);");
                statement.setLong(1, customer.getId());
                statement.setString(2, customer.getName());
                statement.executeUpdate();
        };
    }

    public SqlProcedure getUpdateNameCommand(Customer customer) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("UPDATE customers SET name=? WHERE id=?;");
                statement.setString(1, customer.getName());
                statement.setLong(2, customer.getId());
                statement.executeUpdate();
        };
    }
}
