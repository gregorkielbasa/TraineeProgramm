package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.BasketFixtures;
import org.lager.exception.OrderItemListNotPresentException;
import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.OrderRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.lager.BasketFixtures.defaultBasket;
import static org.lager.BasketFixtures.defaultCustomerNumber;
import static org.lager.OrderFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service")
class OrderServiceTest implements WithAssertions {

    @Mock
    OrderRepository repository;
    @Mock
    BasketService basketService;

    OrderService orderService;

    @Nested
    @DisplayName("gives")
    class GetOrderOrderServiceTest {

        @Test
        @DisplayName("non existing Order")
        void nonExisting() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.getOrder(defaultNumber()))
                    .isEmpty();
        }

        @Test
        @DisplayName("existing Order")
        void properCase() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultOrder()));

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.getOrder(defaultNumber()))
                    .isEqualTo(Optional.of(defaultOrder()));
        }
    }

    @Nested
    @DisplayName("orders")
    class OrderOrderServiceTest {

        @Test
        @DisplayName("non existing / empty Basket")
        void nonExisting() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.when(basketService.getContentOfBasket(defaultNumber()))
                    .thenReturn(Map.of());

            orderService = new OrderService(repository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultNumber()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("null Basket from BasketService")
        void nullBasketContent() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.when(basketService.getContentOfBasket(defaultNumber()))
                    .thenReturn(null);

            orderService = new OrderService(repository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultNumber()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("simply Basket")
        void simplyBasket() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.when(basketService.getContentOfBasket(defaultCustomerNumber()))
                    .thenReturn(defaultBasket().getContent());

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.order(defaultCustomerNumber(), orderDate()))
                    .isEqualTo(defaultOrder());
        }
    }
}