package org.lager.repository.json;

import org.lager.exception.RepositoryException;
import org.lager.model.Order;
import org.lager.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class OrderJsonRepository implements OrderRepository {
    private final JsonEditor jsonEditor;
    private final OrderJsonMapper jsonMapper;
    private final long defaultOrderID = 1000L;
    private final static Logger logger = LoggerFactory.getLogger(OrderJsonRepository.class);

    private final Map<Long, Order> orders;

    public OrderJsonRepository(JsonEditor jsonEditor, OrderJsonMapper jsonMapper) {
        this.jsonEditor = jsonEditor;
        this.jsonMapper = jsonMapper;
        orders = new HashMap<>();
        loadOrderFromFile();
    }

    @Override
    public Optional<Order> read(Long id) {
        validateId(id);
        return Optional.ofNullable(orders.get(id));
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given id is NULL");
    }

    @Override
    public void save(Order order) throws RepositoryException {
        validateOrder(order);
        orders.put(order.getId(), order);
        saveOrdersToFile();
    }

    private void validateOrder(Order order) {
        if (order == null)
            throw new RepositoryException("Given Order is NULL");
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        orders.remove(id);
        saveOrdersToFile();
    }

    @Override
    public long getNextAvailableNumber() {
        return 1 + orders.keySet().stream()
                .max(Long::compareTo)
                .orElse(defaultOrderID - 1);
    }

    private void saveOrdersToFile() {
        List<JsonOrder> jsonRecords = orders.values().stream()
                .map(jsonMapper::orderToJsonRecord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        try {
            jsonEditor.saveToFile(jsonRecords);
        } catch (IOException e) {
            logger.error("Order Repository was not able to save JSON File");
            throw new RepositoryException("Order Repository was not able to save changes in JSON File");
        }
    }

    private void loadOrderFromFile() {
        try {
            List<JsonOrder> jsonRecord = jsonEditor.loadFromFile();
            logger.info("Order Repository has loaded JSON File");

            jsonRecord.stream()
                    .map(jsonMapper::jsonRecordToOrder)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(order -> orders.put(order.getId(), order));
        } catch (IOException e) {
            logger.error("Order Repository was not able to load JSON File");
        }
    }
}
