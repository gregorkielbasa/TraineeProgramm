package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.OrderItemSetNotPresentException;
import org.lager.model.Order;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.OrderService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;
import static org.lager.OrderFixtures.defaultOrder;
import static org.lager.OrderFixtures.defaultOrderId;
import static org.lager.ProductFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated OrderService")
@Transactional
@Rollback
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
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void init() {
        customerService.create(defaultCustomerName());
        productService.create(defaultProductName());
        customerService.create(anotherCustomerName());
        productService.create(anotherProductName());
        basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);
    }

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM ORDER_ITEMS;");
        jdbcTemplate.execute("DELETE FROM ORDERS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS ORDER_KEY RESTART WITH 1000;");

        jdbcTemplate.execute("DELETE FROM BASKET_ITEMS;");
        jdbcTemplate.execute("DELETE FROM BASKETS;");

        jdbcTemplate.execute("DELETE FROM PRODUCTS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS PRODUCT_KEY RESTART WITH 100000000;");

        jdbcTemplate.execute("DELETE FROM CUSTOMERS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS CUSTOMER_KEY RESTART WITH 100000000;");
    }

    @Test
    @DisplayName("orders a basket")
    void properCase() {
        Optional<Order> orderBefore = service.getOrder(defaultOrderId());
        service.order(defaultCustomerId());
        Optional<Order> orderAfter = service.getOrder(defaultOrderId());

        assertThat(orderBefore).isEmpty();
        assertThat(orderAfter).isEqualTo(Optional.of(defaultOrder()));
    }

    @Test
    @DisplayName("orders an empty/non-existing basket")
    void emptyBasket() {
        Optional<Order> orderBefore = service.getOrder(defaultOrderId());
        assertThatThrownBy(() -> service.order(nonExistingCustomerId()))
                .isInstanceOf(OrderItemSetNotPresentException.class);
        Optional<Order> orderAfter = service.getOrder(defaultOrderId());

        assertThat(orderBefore).isEmpty();
        assertThat(orderAfter).isEmpty();
    }
}