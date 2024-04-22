package org.lager.repository.sql;

import org.lager.model.Customer;
import org.lager.repository.xml.BasketXmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class CustomerSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public Optional<Customer> slqToCustomer(ResultSet sqlSet) {
        try {
            if (sqlSet.next()) {
                long id = sqlSet.getLong("id");
                String name = sqlSet.getString("name");

                Customer newCustomer = new Customer(id, name);
                return Optional.of(newCustomer);
            }
        } catch (SQLException e) {
            logger.warn("Customer SQL Mapper was not able to decode Customer");
        }
        return Optional.empty();
    }
}
