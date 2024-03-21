package org.lager.model;

import org.lager.exception.OrderIllegalIDException;
import org.lager.exception.OrderItemListNotPresentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private static final long ID_MIN = 1000;
    private static final long ID_MAX = 9999;

    private final long ID;
    private final long customerNumber;
    private final LocalDateTime dateTime;
    private final List<OrderItem> items;
    private final Logger logger = LoggerFactory.getLogger(Order.class);
    public Order(long ID, long customerNumber, List<OrderItem> items) {
        validateID(ID);
        this.ID = ID;
        this.customerNumber = customerNumber;
        this.dateTime = LocalDateTime.now();
        validateItems(items);
        this.items = items;

        logger.info("New Order {} has been created.", ID);
    }

    private void validateID(long ID) {
        if (ID<ID_MIN || ID>ID_MAX)
            throw new OrderIllegalIDException(ID);
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new OrderItemListNotPresentException(ID);
    }

    public long getID() {
        return ID;
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
}
