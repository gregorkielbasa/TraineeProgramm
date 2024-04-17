package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;

import java.util.Optional;

public class CustomerSqlRepository implements CustomerRepository {
    private final long defaultCustomerId = 100_000_000;

    private final CustomerSqlMapper sqlMapper;
    private final SqlConnector connector;

    public CustomerSqlRepository(CustomerSqlMapper sqlMapper, SqlConnector connector) {
        this.sqlMapper = sqlMapper;
        this.connector = connector;
    }

    @Override
    public long getNextAvailableId() {
        String query = "SELECT MAX(id) FROM customers;";

        return sqlMapper.sqlToId(connector.loadFromDB(query))
                .orElse(defaultCustomerId);
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);
        String query = sqlMapper.CustomerToSqlQuery(customer);

        connector.saveToDB(query);
    }

    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);
        String query = "SELECT * FROM Customers WHERE id=%s;".formatted(id);

        return sqlMapper.slqToCustomer(connector.loadFromDB(query));
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        String query = "DELETE FROM customers WHERE id=%s".formatted(id);

        connector.saveToDB(query);
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
