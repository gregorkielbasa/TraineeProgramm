package org.lager.service;

import org.lager.exception.OrderItemListNotPresentException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final BasketService basketService;
    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository repository, BasketService basketService) {
        this.repository = repository;
        this.basketService = basketService;
    }

    public Optional<Order> getOrder(long orderID) {
        return repository.read(orderID);
    }

    public Order order(long basketId) {
        return order(basketId, LocalDateTime.now());
    }

    public Order order(long basketId, LocalDateTime dataTime) {
        long newOrderId = repository.getNextAvailableId();
        logger.debug("OrderService starts to order {} Basket", basketId);
        List<OrderItem> items = getOrderItemsFromBasket(basketId);
        Order newOrder = new Order(newOrderId, basketId, dataTime, items);
        repository.save(newOrder);
        basketService.dropBasket(basketId);
        logger.debug("OrderService finished to order {} Basket", basketId);
        return newOrder;
    }

    private List<OrderItem> getOrderItemsFromBasket(long basketId) {
        List<OrderItem> result = new ArrayList<>();
        getContentOfBasket(basketId).forEach((productId, amount) -> result.add(new OrderItem(productId, amount)));
        return result;
    }

    private Map<Long, Integer> getContentOfBasket(long basketId) {
        Map<Long, Integer> contentOfBasket = basketService.getContentOfBasket(basketId);
        if (contentOfBasket == null)
            throw new OrderItemListNotPresentException(basketId);
        return contentOfBasket;
    }
}