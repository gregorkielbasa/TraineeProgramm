package org.lager.repository.sql;

import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Optional;

@Component
@Profile("database")
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
                logger.warn("Customer SQL Mapper was not able to decode Customer{}", e.getMessage());
            } catch (CustomerIllegalIdException | CustomerIllegalNameException e) {
                logger.warn("Customer SQL Mapper was not able to create a new Customer{}", e.getMessage());
            }
            return Optional.empty();
        };
    }

    public SqlProcedure getInitialCommand() {
        return connection -> {
            String command = """
                    CREATE TABLE IF NOT EXISTS customers (
                    id bigint PRIMARY KEY,
                    name character varying(16) NOT NULL
                    );""";
            Statement statement = connection.createStatement();
            statement.execute(command);
        };
    }

    public SqlFunction getCustomerWithHighestIdCommand() {
        return connection -> {
            String command = "SELECT * FROM customers ORDER BY id DESC LIMIT 1;";
            Statement statement = connection.createStatement();
            return statement.executeQuery(command);
        };
    }

    public SqlFunction getReadCommand(Long id) {
        return connection -> {
            String command = "SELECT * FROM customers WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteCommand(Long id) {
        return connection -> {
            String command = "DELETE FROM customers WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            statement.executeUpdate();
        };
    }

    public SqlProcedure getInsertCommand(Customer customer) {
        return connection -> {
            String command = "INSERT INTO customers VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, customer.getId());
            statement.setString(2, customer.getName());
            statement.executeUpdate();
        };
    }

    public SqlProcedure getUpdateNameCommand(Customer customer) {
        return connection -> {
            String command = "UPDATE customers SET name=? WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setString(1, customer.getName());
            statement.setLong(2, customer.getId());
            statement.executeUpdate();
        };
    }
}
