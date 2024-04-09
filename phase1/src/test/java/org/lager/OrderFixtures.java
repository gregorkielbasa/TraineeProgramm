package org.lager;

import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.json.JsonOrder;

import java.time.LocalDateTime;
import java.util.List;

public class OrderFixtures {
    private final static long ORDER_NUMBER = 1000;
    private final static long CUSTOMER_NUMBER = CustomerFixtures.defaultNumber();
    private final static OrderItem ITEM_1 = new OrderItem(ProductFixtures.defaultNumber(), 1);
    private final static OrderItem ITEM_2 = new OrderItem(ProductFixtures.anotherNumber(), 2);

    public static long defaultId() {
        return ORDER_NUMBER;
    }

    public static long defaultCustomerNumber() {
        return CUSTOMER_NUMBER;
    }

    public static long incorrectId() {
        return 1;
    }

    public static LocalDateTime orderDate() {
        return LocalDateTime.of(2024, 12, 31, 23, 59);
    }

    public static List<OrderItem> defaultItems() {
        return List.of(ITEM_1, ITEM_2);
    }

    public static Order defaultOrder() {
        return new Order(ORDER_NUMBER, CUSTOMER_NUMBER, orderDate(), List.of(ITEM_1));
    }

    public static Order anotherOrder() {
        return new Order(ORDER_NUMBER+1, CUSTOMER_NUMBER+1, orderDate(), List.of(ITEM_1, ITEM_2));
    }

    public static JsonOrder defaultOrderAsJson() {
        return new JsonOrder(ORDER_NUMBER, CUSTOMER_NUMBER, orderDate(), List.of(ITEM_1));
    }

    public static JsonOrder anotherOrderAsJson() {
        return new JsonOrder(ORDER_NUMBER+1, CUSTOMER_NUMBER+1, orderDate(), List.of(ITEM_1, ITEM_2));
    }
}
