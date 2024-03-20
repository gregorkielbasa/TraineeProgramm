package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.OrderIllegalIDException;
import org.lager.exception.OrderItemListNotPresentException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order")
class OrderTest implements WithAssertions {

    final long VALID_ID = 1234;
    final long INVALID_ID = 12345;
    final long CUSTOMER_NUMBER = 123_123_123L;
    final OrderItem ITEM_1 = new OrderItem(123_123_123, 3);
    final OrderItem ITEM_2 = new OrderItem(123_456_789, 5);
    final List<OrderItem> VALID_ITEM_LIST = List.of(ITEM_1, ITEM_2);

    @Nested
    @DisplayName("throws an exception when")
    class OrderThrowsException {

        @Test
        @DisplayName("created with invalid ID")
        void invalidID() {
            assertThatThrownBy(() -> new Order(INVALID_ID, CUSTOMER_NUMBER, VALID_ITEM_LIST))
                    .isInstanceOf(OrderIllegalIDException.class);
        }

        @Test
        @DisplayName("created with NULL items list")
        void nullItemsList() {
            assertThatThrownBy(() -> new Order(VALID_ID, CUSTOMER_NUMBER, null))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("created with invalid items list")
        void emptyItemList() {
            assertThatThrownBy(() -> new Order(VALID_ID, CUSTOMER_NUMBER, new ArrayList<>()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }
    }

    @Nested
    @DisplayName("works when created with")
    class OrderCustomerNumberTest {

        @Test
        @DisplayName("proper all parameters")
        void properCase() {
            Order order = new Order(VALID_ID, 123_123_123L, VALID_ITEM_LIST);

            assertThat(order.getCustomerNumber()).isEqualTo(123_123_123L);
            assertThat(order.getDateTime()).isEqualToIgnoringNanos(LocalDateTime.now());
            assertThat(order.getItems()).containsExactlyInAnyOrderElementsOf(VALID_ITEM_LIST);
        }

        @Test
        @DisplayName("too short CustomerNumber")
        void tooShort() {
            Order order = new Order(VALID_ID, 123, VALID_ITEM_LIST);

            assertThat(order.getCustomerNumber()).isEqualTo(123);
        }

        @Test
        @DisplayName("too long CustomerNumber")
        void tooLong() {
            Order order = new Order(VALID_ID, 123_123_123_123L, VALID_ITEM_LIST);

            assertThat(order.getCustomerNumber()).isEqualTo(123_123_123_123L);
        }

        @Test
        @DisplayName("too negative CustomerNumber")
        void negative() {
            Order order = new Order(VALID_ID, -123, VALID_ITEM_LIST);

            assertThat(order.getCustomerNumber()).isEqualTo(-123);
        }
    }
}