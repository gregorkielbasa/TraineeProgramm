package org.lager.model;

import org.lager.exception.OrderIllegalIdException;
import org.lager.exception.OrderItemListNotPresentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {
    private static final long ID_MIN = 1000;
    private static final long ID_MAX = 9999;

    private final long id;
    private final long customerNumber;
    private final LocalDateTime dateTime;
    private final List<OrderItem> items;
    private final Logger logger = LoggerFactory.getLogger(Order.class);

    public Order(long id, long customerNumber, List<OrderItem> items) {
        this(id, customerNumber, items, LocalDateTime.now());
    }

    public Order(long id, long customerNumber, List<OrderItem> items, LocalDateTime dateTime) {
        validateId(id);
        this.id = id;
        this.customerNumber = customerNumber;
        this.dateTime = dateTime;
        validateItems(items);
        this.items = items;

        logger.info("New Order {} has been created.", id);
    }

    private void validateId(long id) {
        if (id < ID_MIN || id > ID_MAX)
            throw new OrderIllegalIdException(id);
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new OrderItemListNotPresentException(id);
    }

    public long getId() {
        return id;
    }

    public long getCustomerNumber() {
        return customerNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerNumber, dateTime, items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && customerNumber == order.customerNumber && Objects.equals(dateTime, order.dateTime) && Objects.equals(items, order.items);
    }
}
