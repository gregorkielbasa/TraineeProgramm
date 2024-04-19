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

        connector.sendToDB(query);
    }

    @Override
    public long getNextAvailableId() {
        String query = "SELECT * FROM test ORDER BY id DESC LIMIT 1;";
        Optional<Customer> topCustomer = connector.receiveFromDB(mapper::slqToCustomer, query);
        return topCustomer
                .map(customer -> customer.getId() + 1)
                .orElse(defaultCustomerId);
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);

        if (read(customer.getId()).isPresent())
            update(customer);
        else
            insert(customer);
    }

    private void insert(Customer customer) throws RepositoryException {
        String query = "INSERT INTO Customers VALUES (?, ?);";
        connector.sendToDB(mapper.customerToSqlQuery(query, customer));
    }

    private void update(Customer customer) throws RepositoryException {
        String query = "UPDATE Customers SET (?, ?);";
        connector.sendToDB(mapper.customerToSqlQuery(query, customer));
    }


    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);

        String query = "SELECT * FROM Customers WHERE id=%d;"
                .formatted(id);
        return connector.receiveFromDB(mapper::slqToCustomer, query);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        if (read(id).isPresent()) {
            String query = "DELETE FROM customers WHERE id=%d".formatted(id);
            connector.sendToDB(query);
        }
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
