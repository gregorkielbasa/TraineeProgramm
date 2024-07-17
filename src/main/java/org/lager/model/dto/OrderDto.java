package org.lager.model.dto;

import org.lager.model.Order;
import org.lager.model.OrderItem;

import java.util.Set;

public record OrderDto(long orderId, long customerId, Set<OrderItem> items) {

    public OrderDto (Order order){
        this(order.getOrderId(), order.getCustomerId(), order.getItems());
    }
}
