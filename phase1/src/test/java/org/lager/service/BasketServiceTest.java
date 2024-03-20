package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.NoSuchProductException;
import org.lager.model.Basket;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasketService")
class BasketServiceTest implements WithAssertions {

    BasketService basketService;
    CustomerService customerService;
    ProductService productService;

    @BeforeEach
    void init() {
        customerService = Mockito.mock(CustomerService.class);
        productService = Mockito.mock(ProductService.class);
        basketService = new BasketService(customerService, productService);
    }

    @Test
    @DisplayName("when adds in non-existing Basket")
    void nonExistingBasket() {
        assertThat(basketService.getContentOfBasket(100_100_100)).isEmpty();
    }

    @Nested
    @DisplayName("when adds")
    class addToBasket {

        @Test
        @DisplayName("in non-exisitng Basket")
        void nonExistingBasket() {
            basketService.addToBasket(100_100_100, 200_200_200, 1);

            assertThat(basketService.getContentOfBasket(100_100_100))
                    .containsOnly(Map.entry(200_200_200L, 1));
            Mockito.verify(customerService).validatePresence(100_100_100);
            Mockito.verify(productService).validatePresence(200_200_200);
        }

        @Test
        @DisplayName("in empty Basket")
        void emptyBasket() {
            basketService.addToBasket(100_100_100, 200_200_200, 1);
            basketService.removeFromBasket(100_100_100, 200_200_200);
            basketService.addToBasket(100_100_100, 300_300_300, 1);

            assertThat(basketService.getContentOfBasket(100_100_100))
                    .containsOnly(Map.entry(300_300_300L, 1));
            Mockito.verify(productService).validatePresence(200_200_200);
            Mockito.verify(productService).validatePresence(300_300_300);
            Mockito.verify(customerService).validatePresence(100_100_100);
        }

        @Test
        @DisplayName("non-exisitng Customer")
        void nonExistingCustomer() {
            Mockito.when(customerService.validatePresence(100_100_100))
                    .thenThrow(NoSuchCustomerException.class);

            assertThatThrownBy(() -> basketService.addToBasket(100_100_100, 200_200_200, 1))
                    .isInstanceOf(NoSuchCustomerException.class);
            Mockito.verify(customerService).validatePresence(100_100_100);
            Mockito.verify(productService).validatePresence(200_200_200);
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            Mockito.when(productService.validatePresence(200_200_200))
                    .thenThrow(NoSuchProductException.class);

            assertThatThrownBy(() -> basketService.addToBasket(100_100_100, 200_200_200, 1))
                    .isInstanceOf(NoSuchProductException.class);
            Mockito.verify(productService).validatePresence(200_200_200);
        }
    }

    @Nested
    @DisplayName("when empties")
    class emptyBasket {

        @BeforeEach
        void init() {
            basketService.addToBasket(100_100_100, 200_200_200, 1);
        }

        @Test
        @DisplayName("a NOT empty Basket")
        void NotEmptyBasket() {
            basketService.emptyBasket(100_100_100);

            assertThat(basketService).extracting("baskets").isEqualTo(new HashMap<Long, Basket>());
        }

        @Test
        @DisplayName("an empty Basket")
        void emptyBasket() {
            basketService.removeFromBasket(100_100_100, 200_200_200);
            basketService.emptyBasket(100_100_100);

            assertThat(basketService).extracting("baskets").isEqualTo(new HashMap<Long, Basket>());
        }
    }

    @Nested
    @DisplayName("when removes")
    class removeFromBasket {

        @Test
        @DisplayName("from non-exisitng Basket")
        void nonExistingBasket() {
            basketService.removeFromBasket(100_100_100, 200_200_200);

            assertThat(basketService).extracting("baskets").isEqualTo(new HashMap<Long, Basket>());
        }

        @Test
        @DisplayName("from empty Basket")
        void emptyBasket() {
            basketService.addToBasket(100_100_100, 200_200_200, 1);
            basketService.removeFromBasket(100_100_100, 200_200_200);
            basketService.removeFromBasket(100_100_100, 200_200_200);

            assertThat(basketService.getContentOfBasket(100_100_100)).isEmpty();
            Mockito.verify(customerService).validatePresence(100_100_100);
            Mockito.verify(productService).validatePresence(200_200_200);
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            basketService.removeFromBasket(100_100_100, 0);

            assertThat(basketService).extracting("baskets").isEqualTo(new HashMap<Long, Basket>());
        }

        @Test
        @DisplayName("non-exisitng Customer")
        void nonExistingCustomer() {
            basketService.removeFromBasket(0, 100_100_100);

            assertThat(basketService).extracting("baskets").isEqualTo(new HashMap<Long, Basket>());
        }
    }
}