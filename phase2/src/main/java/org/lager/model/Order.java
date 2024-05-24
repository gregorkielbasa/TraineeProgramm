package org.lager.model;

import org.lager.exception.OrderIllegalIdException;
import org.lager.exception.OrderItemSetNotPresentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Table("ORDERS")
public class Order {
    private static final long ID_MIN = 1000;
    private static final long ID_MAX = 9999;
    private final static Logger logger = LoggerFactory.getLogger(Order.class);

    @Id
    private final long orderId;
    private final long customerId;
    @MappedCollection(idColumn = "ORDER_ID")
    private final Set<OrderItem> items;

    public Order(long customerId, Set<OrderItem> items) {
        this(0, customerId, items);
        logger.info("New Order {} has been created.", customerId);
    }

    @PersistenceCreator
    protected Order(long orderId, long customerId, Collection<OrderItem> items) {
        validateId(orderId);
        this.orderId = orderId;
        this.customerId = customerId;
        validateItems(items);
        this.items = Set.copyOf(items);
    }

    private void validateId(long id) {
        if (id != 0 && (id < ID_MIN || id > ID_MAX))
            throw new OrderIllegalIdException(id);
    }

    private void validateItems(Collection<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new OrderItemSetNotPresentException(orderId);
    }

    public long getOrderId() {
        return orderId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public Set<OrderItem> getItems() {
        return Set.copyOf(items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId && customerId == order.customerId && Objects.equals(items, order.items);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", items=" + items +
                '}';
    }
}
