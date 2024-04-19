package org.lager.repository.sql;

import org.lager.exception.SqlConnectorException;
import org.lager.model.Customer;
import org.lager.repository.xml.BasketXmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomerSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public Optional<Customer> slqToCustomer(ResultSet sqlSet) {
        try {
            if (sqlSet != null && sqlSet.next()) {
                long id = sqlSet.getLong("id");
                String name = sqlSet.getString("name");

                Customer newCustomer = new Customer(id, name);
                return Optional.of(newCustomer);
            }
        } catch (SQLException e) {
            logger.warn("Customer SQL Mapper could not read Customer");
        }
        return Optional.empty();
    }

    public Consumer<Connection> customerToSqlQuery(String query, Customer customer) {
        return (connection) -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, customer.getId());
                statement.setString(2, customer.getName());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
