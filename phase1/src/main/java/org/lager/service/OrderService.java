package org.lager.service;

import org.lager.exception.OrderItemListNotPresentException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderService {

    private long newOrderID = 1000;
    private List<Order> orders;
    private final BasketService basketService;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(BasketService basketService) {
        this.basketService = basketService;
        orders = new LinkedList<>();
    }

    public long order(long customerNumber) {
        logger.debug("OrderService starts to order {} Basket", customerNumber);
        List<OrderItem> items = getOrderItemsFromBasket(customerNumber);
        Order newOrder = new Order(newOrderID, customerNumber, items);
        orders.add(newOrder);
        basketService.emptyBasket(customerNumber);
        logger.debug("OrderService finished to order {} Basket", customerNumber);
        return newOrderID++;
    }

    private List<OrderItem> getOrderItemsFromBasket(long customerNumber) {
        Map<Long, Integer> basketContent = getContentOfBasket(customerNumber);
        return basketContent.entrySet().stream()
                .map((entry) -> new OrderItem(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Map<Long, Integer> getContentOfBasket(long customerNumber) {
        Map<Long, Integer> contentOfBasket = basketService.getContentOfBasket(customerNumber);
        if (contentOfBasket == null)
            throw new OrderItemListNotPresentException(customerNumber);
        return contentOfBasket;
    }

    public Optional<Order> getOrder(long orderID) {
        for (Order record : orders) {
            if (record.getId() == orderID)
                return Optional.of(record);
        }
        return Optional.empty();
    }
}