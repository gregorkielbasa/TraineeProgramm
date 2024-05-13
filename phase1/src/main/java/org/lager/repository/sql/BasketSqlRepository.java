package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BasketSqlRepository implements BasketRepository {

    private final static Logger logger = LoggerFactory.getLogger(BasketSqlRepository.class);
    private final BasketSqlMapper mapper;
    private final SqlConnector connector;

    public BasketSqlRepository(BasketSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        SqlProcedure command = mapper.getInitialCommand();

        try {
            connector.sendToDB(command);
            logger.info("BasketRepository initialised Basket Table");
        } catch (SqlConnectionException e) {
            logger.error("BasketRepository could not initialise Basket Table");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Optional<Basket> read(Long id) {
        validateId(id);
        SqlFunction command = mapper.getReadWholeBasketCommand(id);
        SqlDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();

        try {
            logger.debug("BasketRepository received Basket with {} ID", id);
            return connector.receiveFromDB(command, decoder);
        } catch (SqlConnectionException e) {
            logger.error("BasketRepository could not read Basket with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        SqlProcedure command = mapper.getDeleteWholeBasketCommand(id);

        try {
            connector.sendToDB(command);
            logger.info("BasketRepository deleted Basket with {} ID", id);
        } catch (SqlConnectionException e) {
            logger.error("BasketRepository could not delete Basket with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void save(Basket basket) throws RepositoryException {
        validateBasket(basket);

        if (read(basket.getCustomerId()).isPresent())
            update(basket);
        else
            insert(basket);
    }

    private void insert(Basket basket) throws RepositoryException {
        SqlProcedure[] commands = mapper.getInsertWholeBasketCommands(basket);

        try {
            connector.sendToDB(commands);
            logger.info("BasketRepository inserted Basket with {} ID", basket.getCustomerId());
        } catch (SqlConnectionException e) {
            logger.error("BasketRepository failed to insert Basket with {} ID", basket.getCustomerId());
            throw new RepositoryException(e.getMessage());
        }
    }

    private void update(Basket basket) throws RepositoryException {
        SqlProcedure command1 = mapper.getDeleteWholeBasketCommand(basket.getCustomerId());
        SqlProcedure[] commands2 = mapper.getInsertWholeBasketCommands(basket);

        try {
            connector.sendToDB(command1, commands2);
            logger.info("BasketRepository saved Basket with {} ID", basket.getCustomerId());
        } catch (SqlConnectionException e) {
            logger.error("BasketRepository failed to save Basket with {} ID", basket.getCustomerId());
            throw new RepositoryException(e.getMessage());
        }
    }

    private void validateBasket(Basket basket) {
        if (basket == null)
            throw new RepositoryException("Given Basket is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Basket's ID is NULL");
    }
}
