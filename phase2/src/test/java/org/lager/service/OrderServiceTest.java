package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.OrderItemSetNotPresentException;
import org.lager.model.Order;
import org.lager.repository.OrderRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.lager.BasketFixtures.basketContentOf;
import static org.lager.BasketFixtures.defaultBasket;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.OrderFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

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
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            orderService = new OrderService(repository, basketService);
            Optional<Order> order = orderService.getOrder(defaultOrderId());

            assertThat(order).isEmpty();
            Mockito.verify(repository).findById(defaultOrderId());
        }

        @Test
        @DisplayName("existing Order")
        void properCase() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultOrder()));

            orderService = new OrderService(repository, basketService);
            Optional<Order> order = orderService.getOrder(defaultOrderId());

            assertThat(order).isEqualTo(Optional.of(defaultOrder()));
            Mockito.verify(repository).findById(defaultOrderId());
        }
    }

    @Nested
    @DisplayName("orders")
    class OrderOrderServiceTest {

        @Test
        @DisplayName("simply Basket")
        void simplyBasket() {
            Mockito.when(basketService.getContentOfBasket(anyLong()))
                    .thenReturn(basketContentOf(defaultBasket()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultOrder());

            orderService = new OrderService(repository, basketService);
            Order order = orderService.order(defaultCustomerId());

            assertThat(order).isEqualTo(defaultOrder());
            Mockito.verify(basketService).getContentOfBasket(defaultCustomerId());
            Mockito.verify(repository).save(defaultNewOrder());
            Mockito.verify(basketService).dropBasket(defaultCustomerId());
        }

        @Test
        @DisplayName("non existing / empty Basket")
        void nonExisting() {
            Mockito.when(basketService.getContentOfBasket(anyLong()))
                    .thenReturn(Map.of());

            orderService = new OrderService(repository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultCustomerId()))
                    .isInstanceOf(OrderItemSetNotPresentException.class);

            Mockito.verify(basketService).getContentOfBasket(defaultCustomerId());
        }
    }
}