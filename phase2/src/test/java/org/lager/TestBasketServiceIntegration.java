package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.NoSuchProductException;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.lager.BasketFixtures.*;
import static org.lager.CustomerFixtures.*;
import static org.lager.ProductFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated BasketService")
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:integrationtest.properties")
class TestBasketServiceIntegration implements WithAssertions {

    @Autowired
    BasketService service;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProductService productService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void init() {
        customerService.create(defaultCustomerName());
        productService.create(defaultProductName());
        customerService.create(anotherCustomerName());
        productService.create(anotherProductName());
    }

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM BASKETS;");
        jdbcTemplate.execute("DELETE FROM BASKET_ITEMS;");
        jdbcTemplate.execute("ALTER TABLE BASKETS ALTER COLUMN BASKET_ID RESTART WITH 1000;");

        jdbcTemplate.execute("DELETE FROM PRODUCTS;");
        jdbcTemplate.execute("ALTER TABLE PRODUCTS ALTER COLUMN PRODUCT_ID RESTART WITH 100000000;");

        jdbcTemplate.execute("DELETE FROM CUSTOMERS;");
        jdbcTemplate.execute("ALTER TABLE CUSTOMERS ALTER COLUMN CUSTOMER_ID RESTART WITH 100000000;");
    }

    @DisplayName("adds")
    @Nested
    class TestMethod {
        @Test
        @DisplayName("adds to Basket existing Product")
        public void properCase() {
            //When
            Map<Long, Integer> basketBefore = service.getContentOfBasket(defaultCustomerId());
            service.addToBasket(defaultCustomerId(), defaultProductId(), 1);
            Map<Long, Integer> basketAfter = service.getContentOfBasket(defaultCustomerId());

            //Then
            assertThat(basketBefore).isEmpty();
            assertThat(basketAfter).containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        }

        @Test
        @DisplayName("adds to Basket non-existing Product")
        public void nonExistingProduct() {
            //When
            Map<Long, Integer> basketBefore = service.getContentOfBasket(defaultCustomerId());
            assertThatThrownBy(() -> service.addToBasket(defaultCustomerId(), nonExistingProductId(), 1))
                    .isInstanceOf(NoSuchProductException.class);
            Map<Long, Integer> basketAfter = service.getContentOfBasket(defaultCustomerId());

            //Then
            assertThat(basketBefore).isEmpty();
            assertThat(basketAfter).isEmpty();
        }

        @Test
        @DisplayName("adds to non-existing Customers Basket Product")
        public void nonExistingCustomer() {
            //When
            Map<Long, Integer> basketBefore = service.getContentOfBasket(nonExistingCustomerId());
            assertThatThrownBy(() -> service.addToBasket(nonExistingCustomerId(), defaultProductId(), 1))
                    .isInstanceOf(NoSuchCustomerException.class);
            Map<Long, Integer> basketAfter = service.getContentOfBasket(nonExistingCustomerId());

            //Then
            assertThat(basketBefore).isEmpty();
            assertThat(basketAfter).isEmpty();
        }
    }

    @Test
    @DisplayName("removes from Basket")
    public void removesFromBasketTest() {
        //When
        service.addToBasket(anotherCustomerId(), defaultProductId(), 2);
        service.addToBasket(anotherCustomerId(), anotherProductId(), 3);
        Map<Long, Integer> basketBefore = service.getContentOfBasket(anotherCustomerId());
        service.removeFromBasket(anotherCustomerId(), anotherProductId());
        Map<Long, Integer> basketAfter = service.getContentOfBasket(anotherCustomerId());

        //Then
        assertThat(basketBefore).containsExactlyInAnyOrderEntriesOf(basketContentOf(anotherBasket()));
        assertThat(basketAfter).containsExactlyInAnyOrderEntriesOf(basketContentOf(anotherBasketWith(defaultProductId(), 2)));
    }


    @Test
    @DisplayName("drops whole basket")
    public void dropBasket() {
        //When
        service.addToBasket(defaultCustomerId(), defaultProductId(), 1);
        Map<Long, Integer> basketBefore = service.getContentOfBasket(defaultCustomerId());
        service.dropBasket(defaultCustomerId());
        Map<Long, Integer> basketAfter = service.getContentOfBasket(defaultCustomerId());

//        Then
        assertThat(basketBefore).containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        assertThat(basketAfter).isEmpty();
    }
}