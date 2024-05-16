package org.lager;

import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.json.JsonOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderFixtures {
    private final static long ORDER_ID = 1000;
    private final static long CUSTOMER_ID = CustomerFixtures.defaultId();
    private final static OrderItem ITEM_1 = new OrderItem(ProductFixtures.defaultId(), 1);
    private final static OrderItem ITEM_2 = new OrderItem(ProductFixtures.anotherId(), 2);

    public static long defaultId() {
        return ORDER_ID;
    }

    public static long defaultCustomerId() {
        return CUSTOMER_ID;
    }

    public static long incorrectId() {
        return 1;
    }

    public static LocalDateTime orderDate() {
        return LocalDateTime.of(2024, 12, 31, 23, 59);
    }

    public static List<OrderItem> defaultItemsList() {
        return List.of(ITEM_1, ITEM_2);
    }

    public static Map<Long, Integer> defaultItemsMap() {
        return Map.of(ITEM_1.productId(), ITEM_1.amount(), ITEM_2.productId(), ITEM_2.amount());
    }

    public static Order defaultOrder() {
        return new Order(ORDER_ID, CUSTOMER_ID, orderDate(), List.of(ITEM_1));
    }

    public static Order anotherOrder() {
        return new Order(ORDER_ID +1, CUSTOMER_ID +1, orderDate(), List.of(ITEM_1, ITEM_2));
    }

    public static JsonOrder defaultOrderAsJson() {
        return new JsonOrder(ORDER_ID, CUSTOMER_ID, orderDate(), List.of(ITEM_1));
    }

    public static JsonOrder anotherOrderAsJson() {
        return new JsonOrder(ORDER_ID +1, CUSTOMER_ID +1, orderDate(), List.of(ITEM_1, ITEM_2));
    }
}
