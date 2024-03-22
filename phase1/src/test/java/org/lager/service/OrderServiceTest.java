package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.OrderItemListNotPresentException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest implements WithAssertions {
    OrderItem ITEM_1 = new OrderItem(123_123_123, 3);
    OrderItem ITEM_2 = new OrderItem(123_456_789, 5);
    long ANY_CUSTOMER_NUMBER = 999_999_999L;
    long CUSTOMER_1 = 123_123_123L;
    long CUSTOMER_2 = 222_222_222L;
    Map<Long, Integer> VALID_CONTENT_OF_BASKET_1 = Map.of(123_123_123L, 3, 123_456_789L, 5);
    Map<Long, Integer> VALID_CONTENT_OF_BASKET_2 = Map.of(222_222_222L, 7, 333_333_333L, 9);

    BasketService basketService;
    OrderService orderService;

    @BeforeEach
    void init() {
        basketService = Mockito.mock(BasketService.class);
        orderService = new OrderService(basketService);
    }

    @Nested
    @DisplayName("throws an Exception when ")
    class OrderThrows {

        @Test
        @DisplayName("throws an Exception when ordered with NULL Basket")
        void orderNull() {
            Mockito.when(basketService.getContentOfBasket(ANY_CUSTOMER_NUMBER)).thenReturn(Mockito.any());

            assertThatThrownBy(() -> orderService.order(ANY_CUSTOMER_NUMBER))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("throws an Exception when ordered with NULL Basket")
        void orderEmpty() {
            Mockito.when(basketService.getContentOfBasket(ANY_CUSTOMER_NUMBER)).thenReturn(new HashMap<>());

            assertThatThrownBy(() -> orderService.order(ANY_CUSTOMER_NUMBER))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }
    }

    @Nested
    @DisplayName("when have more Orders")
    class OrderWithTwoOrders {

        @Test
        @DisplayName("gives the right one")
        void orderProperCase() {
            Mockito.when(basketService.getContentOfBasket(CUSTOMER_1)).thenReturn(VALID_CONTENT_OF_BASKET_1);
            Mockito.when(basketService.getContentOfBasket(CUSTOMER_2)).thenReturn(VALID_CONTENT_OF_BASKET_2);

            orderService.order(CUSTOMER_1);
            orderService.order(CUSTOMER_2);

            assertThat(orderService.getOrder(1000).get().getItems())
                    .containsExactlyInAnyOrderElementsOf(List.of(ITEM_1, ITEM_2));
        }

        @Test
        @DisplayName("gives empty")
        void orderNotExisting() {
            Mockito.when(basketService.getContentOfBasket(CUSTOMER_1)).thenReturn(VALID_CONTENT_OF_BASKET_1);
            Mockito.when(basketService.getContentOfBasket(CUSTOMER_2)).thenReturn(VALID_CONTENT_OF_BASKET_2);

            orderService.order(CUSTOMER_1);
            orderService.order(CUSTOMER_2);

            assertThat(orderService.getOrder(9999)).isEmpty();
        }
    }
}