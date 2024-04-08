package org.lager.repository.json;

import org.lager.exception.RepositoryException;
import org.lager.model.Order;
import org.lager.repository.OrderRepository;

import java.util.Optional;

public class OrderJsonRepositoryImpl implements OrderRepository {
    @Override
    public void save(Order entity) throws RepositoryException {

    }

    @Override
    public Optional<Order> read(Long aLong) {
        return Optional.empty();
    }

    @Override
    public void delete(Long aLong) throws RepositoryException {

    }

    @Override
    public long getNextAvailableNumber() {
        return 0;
    }

//    private long newOrderID = 1000;
//    private List<Order> orders;
//    private final BasketService basketService;
//    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
//
//    public OrderService(BasketService basketService) {
//        this.basketService = basketService;
//        orders = new LinkedList<>();
//    }
//
//    public long order(long customerNumber) {
//        logger.debug("OrderService starts to order {} Basket", customerNumber);
//        List<OrderItem> items = getOrderItemsFromBasket(customerNumber);
//        Order newOrder = new Order(newOrderID, customerNumber, items);
//        orders.add(newOrder);
//        basketService.dropBasket(customerNumber);
//        logger.debug("OrderService finished to order {} Basket", customerNumber);
//        return newOrderID++;
//    }
//
//    private List<OrderItem> getOrderItemsFromBasket(long customerNumber) {
//        Map<Long, Integer> basketContent = getContentOfBasket(customerNumber);
//        return basketContent.entrySet().stream()
//                .map((entry) -> new OrderItem(entry.getKey(), entry.getValue()))
//                .toList();
//    }
//
//    private Map<Long, Integer> getContentOfBasket(long customerNumber) {
//        Map<Long, Integer> contentOfBasket = basketService.getContentOfBasket(customerNumber);
//        if (contentOfBasket == null)
//            throw new OrderItemListNotPresentException(customerNumber);
//        return contentOfBasket;
//    }
//
//    public Optional<Order> getOrder(long orderID) {
//        for (Order record : orders) {
//            if (record.getId() == orderID)
//                return Optional.of(record);
//        }
//        return Optional.empty();
//    }
}
