package org.lager.repository.sql;

import org.lager.model.Customer;
import org.lager.repository.xml.BasketXmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public Optional<Customer> slqToCustomer(ResultSet sqlSet) {
        return Optional.empty();
    }

    public String CustomerToSqlQuery(Customer customer) {
        return "%s, %d".formatted(customer.getName(), customer.getId());
    }

    public Optional<Long> sqlToId(ResultSet sqlSet) {
        try {
            if (sqlSet != null && sqlSet.next()) {
                long value = sqlSet.getLong(1);
                return value == 0
                        ? Optional.empty()
                        : Optional.of(value);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
