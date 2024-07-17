package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.lager.exception.OrderItemListNotPresentException;
import org.lager.service.BasketService;
import org.lager.service.OrderService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.lager.OrderFixtures.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@TestPropertySource(locations = "classpath:integrationtest.properties")
@ActiveProfiles("database")
class TestOrderServiceIntegration implements WithAssertions {

    @Autowired
    OrderService service;
    @MockBean
    BasketService basketService;

    @Test
    void emptyBasket() {
        assertThatThrownBy(() -> service.order(incorrectId()))
                .isInstanceOf(OrderItemListNotPresentException.class);
    }

    @Test
    void testBasketServiceOperations() {
        Mockito.when(basketService.getContentOfBasket(anyLong()))
                .thenReturn(defaultItemsMap());

        assertThat(service.getOrder(defaultId()))
                .isEmpty();
        service.order(defaultId());
        assertThat(service.getOrder(defaultId()).get().getItems())
                .containsExactlyInAnyOrderElementsOf(defaultItemsList());

        Mockito.verify(basketService).dropBasket(defaultId());
    }
}