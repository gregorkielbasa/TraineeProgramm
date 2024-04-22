package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.CommandQuery;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

public class CustomerSqlRepository implements CustomerRepository {

    private final CustomerSqlMapper mapper;
    private final SqlConnector<Customer> connector;

    public CustomerSqlRepository(CustomerSqlMapper mapper, SqlConnector<Customer> connector) {
        this.mapper = mapper;
        this.connector = connector;
        initialTables();
    }

    private void initialTables() {
        CommandUpdate command = connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS customers (
                        id bigint PRIMARY KEY,
                        name character varying(24) NOT NULL
                        );""");
            }
        };

        connector.sendToDB(command);
    }

    @Override
    public long getNextAvailableId() {
        long defaultCustomerId = 100_000_000;
        CommandQuery command = connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;")) {
                return statement.executeQuery();
            }
        };

        Optional<Customer> topCustomer = connector.receiveFromDB(command, mapper::slqToCustomer);
        return topCustomer
                .map(customer -> customer.getId() + 1)
                .orElse(defaultCustomerId);
    }

    @Override
    public Optional<Customer> read(Long id) {
        CommandQuery command = connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM Customers WHERE id=?;")) {
                statement.setLong(1, id);
                return statement.executeQuery();
            }
        };

        validateId(id);
        return connector.receiveFromDB(command, mapper::slqToCustomer);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        CommandUpdate command = connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM customers WHERE id=?;")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        };

        if (read(id).isPresent())
            connector.sendToDB(command);
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);

        if (read(customer.getId()).isPresent())
            updateName(customer);
        else
            insert(customer);
    }

    private void insert(Customer customer) throws RepositoryException {
        CommandUpdate command = connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO Customers VALUES (?, ?);")) {
                statement.setLong(1, customer.getId());
                statement.setString(2, customer.getName());
                statement.executeUpdate();
            }
        };

        connector.sendToDB(command);
    }

    private void updateName(Customer customer) throws RepositoryException {
        CommandUpdate command = connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE Customers SET name=? WHERE id=?;")) {
                statement.setString(1, customer.getName());
                statement.setLong(2, customer.getId());
                statement.executeUpdate();
            }
        };

        connector.sendToDB(command);
    }

    private void validateCustomer(Customer customer) {
        if (customer == null)
            throw new RepositoryException("Given Customer is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given ID is NULL");
    }
}
