package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;

import java.util.Optional;

public class CustomerSqlRepository implements CustomerRepository {
    private final long defaultCustomerId = 100_000_000;

    private final CustomerSqlMapper mapper;
    private final SqlConnector connector;

    public CustomerSqlRepository(CustomerSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;
    }

    private void initialTables() {
        String query = """
                CREATE TABLE IF NOT EXISTS customers (
                id bigint PRIMARY KEY,
                name character varying(24) NOT NULL
                );""";

        connector.saveToDB(query);
    }

    @Override
    public long getNextAvailableId() {
        String query = "SELECT MAX(id) FROM customers;";

        return mapper.sqlToId(connector.loadFromDB(query))
                .orElse(defaultCustomerId);
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);
        String query = "INSERT INTO CustomersVALUES (%s);"
                .formatted(mapper.CustomerToSqlQuery(customer));

        connector.saveToDB(query);
    }

    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);
        String query = "SELECT * FROM Customers WHERE id=%s;".formatted(id);

        return mapper.slqToCustomer(connector.loadFromDB(query));
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
