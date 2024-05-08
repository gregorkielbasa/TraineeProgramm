package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Order;
import org.lager.repository.OrderRepository;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;

import java.util.ArrayList;
import java.util.Optional;

public class OrderSqlRepository implements OrderRepository {
    private final OrderSqlMapper mapper;
    private final SqlConnector connector;

    public OrderSqlRepository(OrderSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        CommandUpdate command1 = mapper.getOrderInitialCommand();
        CommandUpdate command2 = mapper.getOrderItemInitialCommand();

        connector.sendToDB(command1, command2);
    }

    @Override
    public long getNextAvailableId() {
        long defaultOrderId = 1000;
        CommandQuery command = mapper.getOrderWithHighestIdCommand();
        ResultSetDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();

        Optional<Order> topOrder = connector.receiveFromDB(command, decoder);

        return topOrder
                .map(order -> order.getId() + 1)
                .orElse(defaultOrderId);
    }

    @Override
    public void save(Order order) throws RepositoryException {
        validateOrder(order);
        if (read(order.getId()).isPresent())
            throw new RepositoryException("Given Order ID is already taken!");

        ArrayList<CommandUpdate> commandQueue = new ArrayList<>();
        commandQueue.add(mapper.getInsertEmptyOrderCommand(order));
        commandQueue.addAll(mapper.getInsertOrderItemsListCommand(order));
        connector.sendToDB(commandQueue.toArray(new CommandUpdate[0]));
    }

    @Override
    public Optional<Order> read(Long id) {
        validateId(id);
        CommandQuery command = mapper.getReadCommand(id);
        ResultSetDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();

        return connector.receiveFromDB(command, decoder);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        throw new RepositoryException("Unsupported Operation - DELETE");
    }

    private void validateOrder(Order order) {
        if (order == null)
            throw new RepositoryException("Given Order is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Order's ID is NULL");
    }
}
