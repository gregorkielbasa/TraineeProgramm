package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.OrderIllegalIdException;
import org.lager.exception.OrderItemListNotPresentException;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Order")
class OrderTest implements WithAssertions {

    final long VALID_ID = 1234;
    final long INVALID_ID = 12345;
    final long CUSTOMER_ID = 123_123_123L;
    final OrderItem ITEM_1 = new OrderItem(123_123_123, 3);
    final OrderItem ITEM_2 = new OrderItem(123_456_789, 5);
    final List<OrderItem> VALID_ITEM_LIST = List.of(ITEM_1, ITEM_2);

    @Nested
    @DisplayName("throws an exception when")
    class OrderThrowsException {

        @Test
        @DisplayName("created with invalid ID")
        void invalidID() {
            assertThatThrownBy(() -> new Order(INVALID_ID, CUSTOMER_ID, VALID_ITEM_LIST))
                    .isInstanceOf(OrderIllegalIdException.class);
        }

        @Test
        @DisplayName("created with NULL items list")
        void nullItemsList() {
            assertThatThrownBy(() -> new Order(VALID_ID, CUSTOMER_ID, null))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("created with invalid items list")
        void emptyItemList() {
            assertThatThrownBy(() -> new Order(VALID_ID, CUSTOMER_ID, new ArrayList<>()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }
    }

    @Nested
    @DisplayName("works when created with")
    class OrderCustomerIdTest {

        @Test
        @DisplayName("proper all parameters")
        void properCase() {
            Order order = new Order(VALID_ID, 123_123_123L, VALID_ITEM_LIST);

            assertThat(order.getCustomerId()).isEqualTo(123_123_123L);
            assertThat(order.getOrderId()).isEqualTo(VALID_ID);
            assertThat(order.getItems()).containsExactlyInAnyOrderElementsOf(VALID_ITEM_LIST);
        }

        @Test
        @DisplayName("too short CustomerId")
        void tooShort() {
            Order order = new Order(VALID_ID, 123, VALID_ITEM_LIST);

            assertThat(order.getCustomerId()).isEqualTo(123);
        }

        @Test
        @DisplayName("too long CustomerId")
        void tooLong() {
            Order order = new Order(VALID_ID, 123_123_123_123L, VALID_ITEM_LIST);

            assertThat(order.getCustomerId()).isEqualTo(123_123_123_123L);
        }

        @Test
        @DisplayName("negative CustomerId")
        void negative() {
            Order order = new Order(VALID_ID, -123, VALID_ITEM_LIST);

            assertThat(order.getCustomerId()).isEqualTo(-123);
        }
    }

    @Nested
    @DisplayName("tests equality")
    class EqualityBasket {

        @Test
        @DisplayName("of the same")
        void theSame() {
            Order order = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order.equals(order)).isTrue();
        }

        @Test
        @DisplayName("of NULL")
        void nullOrder() {
            Order order = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order.equals(null)).isFalse();
        }

        @Test
        @DisplayName("of different Classes")
        void differentClasses() {
            Order order = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order.equals("any")).isFalse();
        }

        @Test
        @DisplayName("os the same object")
        void similarOrder() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order1.equals(order2)).isTrue();
        }

        @Test
        @DisplayName("os the same object")
        void similarOrderWithDifferentId() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID + 1, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order1.equals(order2)).isFalse();
        }

        @Test
        @DisplayName("os the same object")
        void similarOrderWithDifferentCustomer() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID + 1, VALID_ITEM_LIST);

            assertThat(order1.equals(order2)).isFalse();
        }

        @Test
        @DisplayName("os the same object")
        void similarOrderWithDifferentItems() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID, List.of(ITEM_1));

            assertThat(order1.equals(order2)).isFalse();
        }
    }

    @Nested
    @DisplayName("tests its hashCode")
    class HashCodeBasket {

        @Test
        @DisplayName("and they should be the same")
        void similarOrder() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
        }

        @Test
        @DisplayName("with different ID")
        void differentOrderId() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID + 1, CUSTOMER_ID, VALID_ITEM_LIST);

            assertThat(order1.hashCode()).isNotEqualTo(order2.hashCode());
        }

        @Test
        @DisplayName("with different Customers")
        void differentCustomer() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID + 1, VALID_ITEM_LIST);

            assertThat(order1.hashCode()).isNotEqualTo(order2.hashCode());
        }

        @Test
        @DisplayName("with different items")
        void differentItems() {
            Order order1 = new Order(VALID_ID, CUSTOMER_ID, VALID_ITEM_LIST);
            Order order2 = new Order(VALID_ID, CUSTOMER_ID, List.of(ITEM_1));

            assertThat(order1.hashCode()).isNotEqualTo(order2.hashCode());
        }
    }
}