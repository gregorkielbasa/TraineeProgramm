package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchBasketException;
import org.lager.exception.NoSuchProductException;
import org.lager.repository.BasketRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.lager.BasketFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasketService")
class BasketServiceTest implements WithAssertions {

    @Mock
    CustomerService customerService;
    @Mock
    ProductService productService;
    @Mock
    BasketRepository repository;

    BasketService basketService;

    @Nested
    @DisplayName("when check content of")
    class GetContentOfBasketServiceTest {

        @Test
        @DisplayName("existing Basket")
        void existingID() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultBasket()));

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerNumber()))
                    .containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        }

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingID() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerNumber()))
                    .containsExactlyInAnyOrderEntriesOf(Map.of());
        }

        @Test
        @DisplayName("empty Basket")
        void emptyID() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultEmptyBasket()));

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerNumber()))
                    .containsExactlyInAnyOrderEntriesOf(Map.of());
        }
    }

    @Test
    @DisplayName("empties (deletes) a Basket")
    void NotEmptyBasket() {
        Mockito.doNothing().when(repository).delete(defaultCustomerNumber());

        basketService = new BasketService(repository, customerService, productService);

        basketService.emptyBasket(defaultCustomerNumber());
    }

    @Nested
    @DisplayName("removes")
    class removeFromBasket {

        @Test
        @DisplayName("from an existing Basket")
        void emptyBasket() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultEmptyBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerNumber(), defaultProductNumber());
        }

        @Test
        @DisplayName("from a non-existing Basket")
        void nonExistingBasket() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.removeFromBasket(defaultCustomerNumber(), defaultProductNumber()))
                    .isInstanceOf(NoSuchBasketException.class);
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerNumber(), 1L);
        }
    }

    @Nested
    @DisplayName("adds a product to")
    class addToBasket {

        @Test
        @DisplayName("basket but product doesn't exist")
        void nonExistingProduct() {
            Mockito.doThrow(new NoSuchProductException(defaultProductNumber()))
                    .when(productService).validatePresence(defaultProductNumber());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.addToBasket(defaultCustomerNumber(), defaultProductNumber(), 1))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductNumber());
            Mockito.doNothing().when(customerService).validatePresence(defaultCustomerNumber());
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.empty());
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);

            basketService.addToBasket(defaultCustomerNumber(), defaultProductNumber(), 1);
        }

        @Test
        @DisplayName("empty Basket")
        void emptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductNumber());
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultEmptyBasket()));
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);

            basketService.addToBasket(defaultCustomerNumber(), defaultProductNumber(), 1);
        }

        @Test
        @DisplayName("non-empty Basket")
        void nonEmptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductNumber());
            Mockito.when(repository.read(defaultCustomerNumber()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultBasketWith(defaultProductNumber(), 2));

            basketService = new BasketService(repository, customerService, productService);
            basketService.addToBasket(defaultCustomerNumber(), defaultProductNumber(), 1);
        }
    }
}