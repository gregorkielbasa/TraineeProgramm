package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.BasketServiceException;
import org.lager.model.Basket;
import org.lager.model.Customer;
import org.lager.model.Product;
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
        assertThat(basketService.getBasket(100_100_100)).isEmpty();
    }

    @Nested
    @DisplayName("when adds")
    class addToBasket {

        @Test
        @DisplayName("in non-exisitng Basket")
        void nonExistingBasket() {
            Mockito.when(customerService.search(100_100_100)).thenReturn(new Customer(123_123_123, "name"));
            Mockito.when(productService.search(200_200_200)).thenReturn(new Product(123_123_123, "name"));
            basketService.addToBasket(100_100_100, 200_200_200, 1);

            assertThat(basketService.getBasket(100_100_100)).containsOnly(Map.entry(200_200_200L, 1));
        }

        @Test
        @DisplayName("in empty Basket")
        void emptyBasket() {
            Mockito.when(customerService.search(100_100_100)).thenReturn(new Customer(123_123_123, "name"));
            Mockito.when(productService.search(200_200_200)).thenReturn(new Product(123_123_123, "name"));
            basketService.addToBasket(100_100_100, 200_200_200, 1);
            basketService.removeFromBasket(100_100_100, 200_200_200);
            basketService.addToBasket(100_100_100, 200_200_200, 1);

            assertThat(basketService.getBasket(100_100_100)).containsOnly(Map.entry(200_200_200L, 1));
        }

        @Test
        @DisplayName("non-exisitng Customer")
        void nonExistingCustomer() {
            Mockito.when(customerService.search(100_100_100)).thenReturn(null);

            assertThatThrownBy(() -> basketService.addToBasket(100_100_100, 200_200_200, 1))
                    .isInstanceOf(BasketServiceException.class);
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            Mockito.when(customerService.search(100_100_100)).thenReturn(new Customer(123_123_123, "name"));
            Mockito.when(productService.search(200_200_200)).thenReturn(null);

            assertThatThrownBy(() -> basketService.addToBasket(100_100_100, 200_200_200, 1))
                    .isInstanceOf(BasketServiceException.class);
        }
    }

    @Nested
    @DisplayName("when empties")
    class emptyBasket {

        @BeforeEach
        void init() {
            Mockito.when(customerService.search(100_100_100)).thenReturn(new Customer(123_123_123, "name"));
            Mockito.when(productService.search(200_200_200)).thenReturn(new Product(123_123_123, "name"));
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
            Mockito.when(customerService.search(100_100_100)).thenReturn(new Customer(123_123_123, "name"));
            Mockito.when(productService.search(200_200_200)).thenReturn(new Product(123_123_123, "name"));

            basketService.addToBasket(100_100_100, 200_200_200, 1);
            basketService.removeFromBasket(100_100_100, 200_200_200);
            basketService.removeFromBasket(100_100_100, 200_200_200);

            assertThat(basketService.getBasket(100_100_100)).isEmpty();
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