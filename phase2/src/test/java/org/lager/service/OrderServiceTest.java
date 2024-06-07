package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchOrderException;
import org.lager.exception.OrderItemSetNotPresentException;
import org.lager.model.dto.OrderDto;
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
    OrderRepository orderRepository;
    @Mock
    BasketService basketService;

    OrderService orderService;

    @Nested
    @DisplayName("gives")
    class GetOrderOrderServiceTest {

        @Test
        @DisplayName("non existing Order")
        void nonExisting() {
            Mockito.when(orderRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            orderService = new OrderService(orderRepository, basketService);
            assertThatThrownBy(() -> orderService.get(defaultOrderId()))
                    .isInstanceOf(NoSuchOrderException.class);

            Mockito.verify(orderRepository).findById(defaultOrderId());
        }

        @Test
        @DisplayName("existing Order")
        void properCase() throws NoSuchOrderException {
            Mockito.when(orderRepository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultOrder()));

            orderService = new OrderService(orderRepository, basketService);
            OrderDto order = orderService.get(defaultOrderId());

            assertThat(order).isEqualTo(new OrderDto(defaultOrder()));
            Mockito.verify(orderRepository).findById(defaultOrderId());
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
            Mockito.when(orderRepository.save(any()))
                    .thenReturn(defaultOrder());

            orderService = new OrderService(orderRepository, basketService);
            OrderDto order = orderService.order(defaultCustomerId());

            assertThat(order).isEqualTo(new OrderDto(defaultOrder()));
            Mockito.verify(basketService).getContentOfBasket(defaultCustomerId());
            Mockito.verify(orderRepository).save(defaultNewOrder());
            Mockito.verify(basketService).dropBasket(defaultCustomerId());
        }

        @Test
        @DisplayName("non existing / empty Basket")
        void nonExisting() {
            Mockito.when(basketService.getContentOfBasket(anyLong()))
                    .thenReturn(Map.of());

            orderService = new OrderService(orderRepository, basketService);

            assertThatThrownBy(() -> orderService.order(defaultCustomerId()))
                    .isInstanceOf(OrderItemSetNotPresentException.class);

            Mockito.verify(basketService).getContentOfBasket(defaultCustomerId());
        }
    }
}