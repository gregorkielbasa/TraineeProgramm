package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;

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
        CommandUpdate command = mapper.getInitialCommand();

        connector.sendToDB(command);
    }

    @Override
    public long getNextAvailableId() {
        long defaultCustomerId = 100_000_000;
        CommandQuery command = mapper.getCustomerWithHighestIdCommand();
        ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();

        Optional<Customer> topCustomer = connector.receiveFromDB(command, decoder);

        return topCustomer
                .map(customer -> customer.getId() + 1)
                .orElse(defaultCustomerId);
    }

    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);
        CommandQuery command = mapper.getReadCommand(id);
        ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();

        return connector.receiveFromDB(command, decoder);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        CommandUpdate command = mapper.getDeleteCommand(id);

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
        CommandUpdate command = mapper.getInsertCommand(customer);

        connector.sendToDB(command);
    }

    private void updateName(Customer customer) throws RepositoryException {
        CommandUpdate command = mapper.getUpdateNameCommand(customer);

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
