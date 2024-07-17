package org.lager.service;

import org.lager.exception.NoSuchOrderException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.model.dto.OrderDto;
import org.lager.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    private Optional<Order> find(long orderId) {
        return repository.findById(orderId);
    }

    public OrderDto get(long orderId) throws NoSuchOrderException {
        return find(orderId)
                .map(OrderDto::new)
                .orElseThrow(() -> new NoSuchOrderException(orderId));
    }

    public List<Long> getAllIds() {
        return repository.getAllIds();
    }

    @Transactional
    public OrderDto order(long customerId) {
        logger.debug("OrderService starts to order {} Basket", customerId);
        Set<OrderItem> items = getOrderItemsFromBasket(customerId);
        Order newOrder = repository.save(new Order(customerId, items));
        basketService.dropBasket(customerId);
        logger.debug("OrderService finished to order {} Basket", customerId);
        return new OrderDto(newOrder);
    }

    private Set<OrderItem> getOrderItemsFromBasket(long customerId) {
        return basketService.getContentOfBasket(customerId).entrySet().stream()
                .map(entry -> new OrderItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }
}