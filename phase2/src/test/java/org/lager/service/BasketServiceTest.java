package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchBasketException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.NoSuchProductException;
import org.lager.repository.BasketRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.lager.BasketFixtures.*;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.ProductFixtures.anotherProductId;
import static org.lager.ProductFixtures.defaultProductId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

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
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.of(defaultBasket()));

            basketService = new BasketService(repository, customerService, productService);
            Map<Long, Integer> items = basketService.getContentOfBasket(defaultCustomerId());

            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            assertThat(items).containsExactlyInAnyOrderEntriesOf(basketContentOf(defaultBasket()));
        }

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingID() {
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);
            Map<Long, Integer> items = basketService.getContentOfBasket(defaultCustomerId());

            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            assertThat(items).isEmpty();
        }

        @Test
        @DisplayName("empty Basket")
        void emptyID() {Mockito.when(repository.findByCustomerId(anyLong()))
                .thenReturn(Optional.of(defaultEmptyBasket()));

            basketService = new BasketService(repository, customerService, productService);
            Map<Long, Integer> items = basketService.getContentOfBasket(defaultCustomerId());

            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            assertThat(items).isEmpty();
        }
    }

    @Test
    @DisplayName("drop (deletes) a Basket")
    void NotEmptyBasket() {
        Mockito.doNothing().when(repository).deleteByCustomerId(anyLong());

        basketService = new BasketService(repository, customerService, productService);
        basketService.dropBasket(defaultCustomerId());

        Mockito.verify(repository).deleteByCustomerId(defaultCustomerId());
    }

    @Nested
    @DisplayName("removes a product")
    class removeFromBasket {

        @Test
        @DisplayName("from an existing Basket")
        void emptyBasket() {
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultEmptyBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerId(), defaultProductId());

            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            Mockito.verify(repository).save(defaultEmptyBasket());
        }

        @Test
        @DisplayName("from a non-existing Basket")
        void nonExistingBasket() {
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.empty());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.removeFromBasket(defaultCustomerId(), defaultProductId()))
                    .isInstanceOf(NoSuchBasketException.class);

            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
        }

        @Test
        @DisplayName("non-exisitng Product")
        void nonExistingProduct() {
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.removeFromBasket(defaultCustomerId(), anotherProductId());


            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            Mockito.verify(repository).save(defaultBasket());
        }
    }

    @Nested
    @DisplayName("adds a product to")
    class addToBasket {

        @Test
        @DisplayName("non-existing Basket")
        void nonExistingBasket() {
            Mockito.doNothing().when(productService).validatePresence(anyLong());
            Mockito.doNothing().when(customerService).validatePresence(anyLong());
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.empty());
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);

            Mockito.verify(productService).validatePresence(defaultProductId());
            Mockito.verify(customerService).validatePresence(defaultCustomerId());
            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            Mockito.verify(repository).save(defaultNewBasket());
        }

        @Test
        @DisplayName("empty Basket")
        void emptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(anyLong());
            Mockito.when(repository.findByCustomerId(anyLong()))
                    .thenReturn(Optional.of(defaultEmptyBasket()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultBasket());

            basketService = new BasketService(repository, customerService, productService);
            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);

            Mockito.verify(productService).validatePresence(defaultProductId());
            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            Mockito.verify(repository).save(defaultBasket());
        }

        @Test
        @DisplayName("non-empty Basket")
        void nonEmptyBasket() {
            Mockito.doNothing().when(productService).validatePresence(anyLong());
            Mockito.when(repository.findByCustomerId(defaultCustomerId()))
                    .thenReturn(Optional.of(defaultBasket()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultBasketWith(defaultProductId(), 2));

            basketService = new BasketService(repository, customerService, productService);
            basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1);

            Mockito.verify(productService).validatePresence(defaultProductId());
            Mockito.verify(repository).findByCustomerId(defaultCustomerId());
            Mockito.verify(repository).save(defaultBasketWith(defaultProductId(), 2));
        }

        @Test
        @DisplayName("basket but product doesn't exist")
        void nonExistingProduct() {
            Mockito.doThrow(new NoSuchProductException(defaultProductId()))
                    .when(productService).validatePresence(anyLong());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(productService).validatePresence(defaultProductId());
        }

        @Test
        @DisplayName("basket but customer doesn't exist")
        void nonExistingCustomer() {
            Mockito.doNothing().when(productService).validatePresence(anyLong());
            Mockito.doThrow(new NoSuchCustomerException(defaultCustomerId()))
                    .when(customerService).validatePresence(anyLong());

            basketService = new BasketService(repository, customerService, productService);

            assertThatThrownBy(() -> basketService.addToBasket(defaultCustomerId(), defaultProductId(), 1))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(productService).validatePresence(defaultProductId());
            Mockito.verify(customerService).validatePresence(defaultCustomerId());
        }
    }
}