package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.OrderItemListNotPresentException;
import org.lager.repository.OrderRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

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
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.empty());

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.getOrder(defaultId()))
                    .isEmpty();
        }

        @Test
        @DisplayName("existing Order")
        void properCase() {
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.of(defaultOrder()));

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.getOrder(defaultId()))
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
                    .thenReturn(defaultId());
            Mockito.when(basketService.getContentOfBasket(defaultId()))
                    .thenReturn(Map.of());

            orderService = new OrderService(repository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultId()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("null Basket from BasketService")
        void nullBasketContent() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultId());
            Mockito.when(basketService.getContentOfBasket(defaultId()))
                    .thenReturn(null);

            orderService = new OrderService(repository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultId()))
                    .isInstanceOf(OrderItemListNotPresentException.class);
        }

        @Test
        @DisplayName("simply Basket")
        void simplyBasket() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultId());
            Mockito.when(basketService.getContentOfBasket(defaultCustomerNumber()))
                    .thenReturn(defaultBasket().getContent());

            orderService = new OrderService(repository, basketService);

            assertThat(orderService.order(defaultCustomerNumber(), orderDate()))
                    .isEqualTo(defaultOrder());
        }
    }
}