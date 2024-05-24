package org.lager.service;

import org.lager.exception.OrderItemListNotPresentException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;
    private final BasketService basketService;

    public OrderService(OrderRepository repository, BasketService basketService) {
        this.repository = repository;
        this.basketService = basketService;
    }

    public Optional<Order> getOrder(long orderID) {
        return repository.findById(orderID);
    }

    public Order order(long customerId) {
        logger.debug("OrderService starts to order {} Basket", customerId);
        Set<OrderItem> items = getOrderItemsFromBasket(customerId);
        Order newOrder = new Order(customerId, items);
        repository.save(newOrder);
        basketService.dropBasket(customerId);
        logger.debug("OrderService finished to order {} Basket", customerId);
        return newOrder;
    }

    private Set<OrderItem> getOrderItemsFromBasket(long customerId) {
        return getContentOfBasket(customerId).entrySet().stream()
                .map(entry -> new OrderItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private Map<Long, Integer> getContentOfBasket(long customerId) {
        Map<Long, Integer> contentOfBasket = basketService.getContentOfBasket(customerId);
        if (contentOfBasket.isEmpty())
            throw new OrderItemListNotPresentException(customerId);
        return contentOfBasket;
    }
}