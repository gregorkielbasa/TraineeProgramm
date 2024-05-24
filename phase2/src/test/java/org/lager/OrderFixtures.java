package org.lager;

import org.lager.model.Order;
import org.lager.model.OrderItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.lager.CustomerFixtures.anotherCustomerId;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.ProductFixtures.anotherProductId;
import static org.lager.ProductFixtures.defaultProductId;

public class OrderFixtures {
    private final static long ORDER_1_ID = 1000;
    private final static long ORDER_2_ID = 1001;
    private final static long CUSTOMER_1_ID = defaultCustomerId();
    private final static long CUSTOMER_2_ID = anotherCustomerId();
    private final static OrderItem ITEM_1 = new OrderItem(defaultProductId(), 1);
    private final static OrderItem ITEM_2 = new OrderItem(anotherProductId(), 2);

    public static Order defaultOrder() {
        return new Order(ORDER_1_ID, CUSTOMER_1_ID, List.of(ITEM_1));
    }

    public static Order defaultNewOrder() {
        return new Order(0, CUSTOMER_1_ID, List.of(ITEM_1));
    }

    public static Order anotherOrder() {
        return new Order(ORDER_2_ID, CUSTOMER_2_ID, List.of(ITEM_1, ITEM_2));
    }

    public static Map<Long, OrderItem> defaultOrderContent() {
        return Map.of(ITEM_1.productId(), ITEM_1);
    }

    public static Map<Long, OrderItem> anotherOrderContent() {
        return Map.of(ITEM_1.productId(), ITEM_1, ITEM_2.productId(), ITEM_2);
    }

    public static Set<OrderItem> defaultOrderItems() {
        return Set.of(ITEM_1);
    }

    public static Set<OrderItem> anotherOrderItems() {
        return Set.of(ITEM_1, ITEM_2);
    }
}
