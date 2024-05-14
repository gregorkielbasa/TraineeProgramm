package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("database")
public class CustomerSqlRepository implements CustomerRepository {

    private final static Logger logger = LoggerFactory.getLogger(CustomerSqlRepository.class);
    private final CustomerSqlMapper mapper;
    private final SqlConnector connector;

    public CustomerSqlRepository(CustomerSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        SqlProcedure command = mapper.getInitialCommand();

        try {
            connector.sendToDB(command);
            logger.info("CustomerRepository initialised Customer Table");
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository could not initialise Customer Table");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public long getNextAvailableId() {
        long defaultCustomerId = 100_000_000;
        SqlFunction command = mapper.getCustomerWithHighestIdCommand();
        SqlDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();

        try {
            Optional<Customer> topCustomer = connector.receiveFromDB(command, decoder);
            logger.debug("CustomerRepository received Customer with highest ID");
            return topCustomer
                    .map(customer -> customer.getId() + 1)
                    .orElse(defaultCustomerId);
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository could not read Customer with highest ID");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);
        SqlFunction command = mapper.getReadCommand(id);
        SqlDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();

        try {
            logger.debug("CustomerRepository received Customer with {} ID", id);
            return connector.receiveFromDB(command, decoder);
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository could not read Customer with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        SqlProcedure command = mapper.getDeleteCommand(id);

        try {
            connector.sendToDB(command);
            logger.info("CustomerRepository deleted Customer with {} ID", id);
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository could not delete Customer with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
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
        SqlProcedure command = mapper.getInsertCommand(customer);

        try {
            connector.sendToDB(command);
            logger.info("CustomerRepository inserted Customer with {} ID", customer.getId());
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository failed to insert Customer with {} ID", customer.getId());
            throw new RepositoryException(e.getMessage());
        }
    }

    private void updateName(Customer customer) throws RepositoryException {
        SqlProcedure command = mapper.getUpdateNameCommand(customer);

        try {
            connector.sendToDB(command);
            logger.info("CustomerRepository updated Customer with {} ID", customer.getId());
        } catch (SqlConnectionException e) {
            logger.error("CustomerRepository failed to update Customer with {} ID", customer.getId());
            throw new RepositoryException(e.getMessage());
        }
    }

    private void validateCustomer(Customer customer) {
        if (customer == null)
            throw new RepositoryException("Given Customer is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Customer's ID is NULL");
    }
}
