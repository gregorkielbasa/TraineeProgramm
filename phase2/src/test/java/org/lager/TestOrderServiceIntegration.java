package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchOrderException;
import org.lager.exception.OrderItemSetNotPresentException;
import org.lager.model.dto.OrderDto;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.lager.CustomerFixtures.*;
import static org.lager.OrderFixtures.defaultOrder;
import static org.lager.OrderFixtures.defaultOrderId;
import static org.lager.ProductFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated OrderService")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtest.properties")
class TestOrderServiceIntegration implements WithAssertions {

    @Autowired
    OrderService service;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProductService productService;
    @Autowired
    BasketService basketService;

    @BeforeEach
    public void init() {
        customerService.create(defaultCustomerName());
        productService.create(defaultProductName());
        customerService.create(anotherCustomerName());
        productService.create(anotherProductName());
        basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);
    }

    @Test
    @DisplayName("orders a basket")
    void properCase() throws NoSuchOrderException {
        assertThatThrownBy(() -> service.get(defaultOrderId()))
                .isInstanceOf(NoSuchOrderException.class);
        service.order(defaultCustomerId());
        OrderDto orderAfter = service.get(defaultOrderId());

        assertThat(orderAfter).isEqualTo(new OrderDto(defaultOrder()));
    }

    @Test
    @DisplayName("orders an empty/non-existing basket")
    void emptyBasket() {
        assertThatThrownBy(() -> service.get(defaultOrderId()))
                .isInstanceOf(NoSuchOrderException.class);
        assertThatThrownBy(() -> service.order(nonExistingCustomerId()))
                .isInstanceOf(OrderItemSetNotPresentException.class);
        assertThatThrownBy(() -> service.get(defaultOrderId()))
                .isInstanceOf(NoSuchOrderException.class);
    }
}