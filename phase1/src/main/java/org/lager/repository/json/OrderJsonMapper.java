package org.lager.repository.json;

import org.lager.exception.OrderIllegalIdException;
import org.lager.exception.OrderItemListNotPresentException;
import org.lager.exception.OrderTimeNullException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderJsonMapper {
    private final static Logger logger = LoggerFactory.getLogger(OrderJsonMapper.class);

    public Optional<Order> jsonRecordToOrder(JsonOrder jsonRecord) {
        Optional<Order> result = Optional.empty();

        try {
            result = Optional.of(extractOrder(jsonRecord));
        } catch (NullPointerException e) {
            logger.warn("Order JSON Record is or contains NULL fields: {}", e.getMessage());
        } catch (OrderIllegalIdException | OrderTimeNullException | OrderItemListNotPresentException e) {
            logger.warn("Order JSON Record is invalid: {}", e.getMessage());
        }

        return result;
    }

    private Order extractOrder(JsonOrder jsonRecord) {
        if (jsonRecord == null)
            throw new NullPointerException("Order JSON Record is NULL");

        long id = jsonRecord.id();
        long customerNumber = jsonRecord.customerNumber();
        List<OrderItem > items = jsonRecord.items();
        LocalDateTime dateTime = jsonRecord.dateTime();

        return new Order(id, customerNumber, dateTime, items);
    }

    public Optional<JsonOrder> orderToJsonRecord(Order order) {
        Optional<JsonOrder> result = Optional.empty();

        try {
            result = Optional.of(extractJson(order));
        } catch (NullPointerException e) {
            logger.warn("Order Record is or contains NULL fields: {}", e.getMessage());
        }

        return result;
    }

    private static JsonOrder extractJson(Order order) {
        if (order == null)
            throw new NullPointerException("Order is NULL");

        long id = order.getId();
        long customerNumber = order.getCustomerNumber();
        LocalDateTime dateTime = order.getDateTime();
        List<OrderItem> items = order.getItems();

        return new JsonOrder(id, customerNumber, dateTime, items);
    }
}
