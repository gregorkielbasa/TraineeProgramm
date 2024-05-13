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
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultBasket()));

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerId()))
                    .containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        }

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingID() {
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerId()))
                    .containsExactlyInAnyOrderEntriesOf(Map.of());
        }

        @Test
        @DisplayName("empty Basket")
        void emptyID() {
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultEmptyBasket()));

            basketService = new BasketService(repository, customerService, productService);

            assertThat(basketService.getContentOfBasket(defaultCustomerId()))
                    .containsExactlyInAnyOrderEntriesOf(Map.of());
        }
    }

    @Test
    @DisplayName("drop (deletes) a Basket")
    void NotEmptyBasket() {
        Mockito.doNothing().when(repository).delete(defaultCustomerId());

        basketService = new BasketService(repository, customerService, productService);

        basketService.dropBasket(defaultCustomerId());
    }

    @Nested
    @DisplayName("removes")
    class removeFromBasket {

        @Test
        @DisplayName("from an existing Basket")
        void emptyBasket() {
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultEmptyBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerId(), defaultProductId());
        }

        @Test
        @DisplayName("from a non-existing Basket")
        void nonExistingBasket() {
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.removeFromBasket(defaultCustomerId(), defaultProductId()))
                    .isInstanceOf(NoSuchBasketException.class);
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerId(), 1L);
        }
    }

    @Nested
    @DisplayName("adds a product to")
    class addToBasket {

        @Test
        @DisplayName("basket but product doesn't exist")
        void nonExistingProduct() {
            Mockito.doThrow(new NoSuchProductException(defaultProductId()))
                    .when(productService).validatePresence(defaultProductId());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductId());
            Mockito.doNothing().when(customerService).validatePresence(defaultCustomerId());
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.empty());
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);

            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);
        }

        @Test
        @DisplayName("empty Basket")
        void emptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductId());
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultEmptyBasket()));
            Mockito.doNothing().when(repository).save(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);

            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);
        }

        @Test
        @DisplayName("non-empty Basket")
        void nonEmptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(defaultProductId());
            Mockito.when(repository.read(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.doNothing().when(repository).save(defaultBasketWith(defaultProductId(), 2));

            basketService = new BasketService(repository, customerService, productService);
            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);
        }
    }
}