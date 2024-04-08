package org.lager;

import org.lager.model.Order;
import org.lager.model.OrderItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderFixtures {
    private final static long ORDER_1_NUMBER = 1000;
    private final static OrderItem ITEM_1 = new OrderItem(ProductFixtures.defaultNumber(), 1);
    private final static long ORDER_2_NUMBER = 1001;
    private final static OrderItem ITEM_2 = new OrderItem(ProductFixtures.anotherNumber(), 2);

    public static long defaultNumber() {
        return ORDER_1_NUMBER;
    }

    public static long anotherNumber() {
        return ORDER_2_NUMBER;
    }

    public static long incorrectNumber() {
        return 1;
    }

    public static Order defaultOrder() {
        return new Order(ORDER_1_NUMBER, CustomerFixtures.defaultNumber(), List.of(ITEM_1), orderDate());
    }

    public static Order anotherOrder() {
        return new Order(ORDER_1_NUMBER, CustomerFixtures.defaultNumber(), List.of(ITEM_1, ITEM_2), orderDate());
    }

    public static LocalDateTime orderDate() {
        return LocalDateTime.of(2024, 12, 31, 23, 59);
    }
}
