package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.NoSuchProductException;
import org.lager.service.BasketService;
import org.lager.service.CustomerService;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.lager.BasketFixtures.*;
import static org.lager.CustomerFixtures.*;
import static org.lager.ProductFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated BasketService")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:integrationtest.properties")
class TestBasketServiceIntegration implements WithAssertions {

    @Autowired
    BasketService service;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProductService productService;

    @BeforeEach
    public void init() {
        customerService.create(defaultCustomerName());
        productService.create(defaultProductName());
        customerService.create(anotherCustomerName());
        productService.create(anotherProductName());
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

    @Test
    @DisplayName("drops basket when customer is deleted")
    void checkIfBasketIsDroppedWhenCustomerIsDeleted() {
        //When
        customerService.create(anotherCustomerName());
        service.addToBasket(anotherCustomerId(), defaultProductId(), 1);
        Map<Long, Integer> basketBefore = service.getContentOfBasket(anotherCustomerId());
        customerService.delete(anotherCustomerId());
        Map<Long, Integer> basketAfter = service.getContentOfBasket(anotherCustomerId());

        //Then
        assertThat(basketBefore).containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        assertThat(basketAfter).isEmpty();
    }
}