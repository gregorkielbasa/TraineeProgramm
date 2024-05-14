package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Order;
import org.lager.repository.OrderRepository;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@Profile("database")
public class OrderSqlRepository implements OrderRepository {

    private final static Logger logger = LoggerFactory.getLogger(OrderSqlRepository.class);
    private final OrderSqlMapper mapper;
    private final SqlConnector connector;

    public OrderSqlRepository(OrderSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        SqlProcedure command1 = mapper.getOrderInitialCommand();
        SqlProcedure command2 = mapper.getOrderItemInitialCommand();

        try {
            connector.sendToDB(command1, command2);
            logger.info("OrderRepository initialised Order and OrderItems Table");
        } catch (SqlConnectionException e) {
            logger.error("OrderRepository could not initialise Order and OrderItems Table");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public long getNextAvailableId() {
        long defaultOrderId = 1000;
        SqlFunction command = mapper.getOrderWithHighestIdCommand();
        SqlDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();

        try {
            Optional<Order> topOrder = connector.receiveFromDB(command, decoder);
            logger.debug("ProductRepository received Product with highest ID");
            return topOrder
                    .map(order -> order.getId() + 1)
                    .orElse(defaultOrderId);
        } catch (SqlConnectionException e) {
            logger.error("OrderRepository could not read Order with highest ID");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Optional<Order> read(Long id) {
        validateId(id);
        SqlFunction command = mapper.getReadCommand(id);
        SqlDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();

        try {
            logger.debug("OrderRepository received Order with {} ID", id);
            return connector.receiveFromDB(command, decoder);
        } catch (SqlConnectionException e) {
            logger.error("OrderRepository could not read Order with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void save(Order order) throws RepositoryException {
        validateOrder(order);
        SqlProcedure[] commands = mapper.getInsertOrderCommands(order);

        if (read(order.getId()).isPresent())
            throw new RepositoryException("Given Order ID is already taken!");

        try {
            connector.sendToDB(commands);
            logger.info("OrderRepository saved Order with {} ID", order.getCustomerId());
        } catch (SqlConnectionException e) {
            logger.error("OrderRepository failed to save Order with {} ID", order.getCustomerId());
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        throw new RepositoryException("Unsupported Operation - DELETE");
    }

    private void validateOrder(Order order) throws RepositoryException {
        if (order == null)
            throw new RepositoryException("Given Order is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Order's ID is NULL");
    }
}
