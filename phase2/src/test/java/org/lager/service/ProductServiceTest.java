package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchProductException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.dto.ProductDto;
import org.lager.repository.ProductRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.ProductFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service")
class ProductServiceTest implements WithAssertions {

    @Mock
    private ProductRepository repository;

    ProductService productService;

    @Nested
    @DisplayName("tests create() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one Product")
        void properOne() {
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultProduct());

            productService = new ProductService(repository);
            ProductDto product = productService.create(defaultProductName());

            assertThat(product).isEqualTo(new ProductDto(defaultProduct()));
            Mockito.verify(repository).save(defaultNewProduct());
        }


        @Test
        @DisplayName("a product with null Name should throw an exception")
        void nullName() {
            productService = new ProductService(repository);

            assertThatThrownBy(() -> productService.create(null))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product with invalid Name should throw an exception")
        void invalidName() {
            productService = new ProductService(repository);

            assertThatThrownBy(() -> productService.create(incorrectProductName()))
                    .isInstanceOf(ProductIllegalNameException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultProduct()));

            productService = new ProductService(repository);
            ProductDto product = productService.get(defaultProductId());

            assertThat(product).isEqualTo(new ProductDto(defaultProduct()));
            Mockito.verify(repository).findById(defaultProductId());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.get(defaultProductId()))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(defaultProductId());
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.get(incorrectProductId()))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(incorrectProductId());
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultProduct()));

            productService = new ProductService(repository);
            productService.validatePresence(defaultProductId());

            Mockito.verify(repository).findById(defaultProductId());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.validatePresence(defaultProductId()))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(defaultProductId());
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.validatePresence(incorrectProductId()))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(incorrectProductId());
        }
    }

    @Nested
    @DisplayName("when deletes")
    class RemoveProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.doNothing().when(repository).deleteById(anyLong());

            productService = new ProductService(repository);
            productService.delete(defaultProductId());

            Mockito.verify(repository).deleteById(defaultProductId());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).deleteById(anyLong());

            productService = new ProductService(repository);
            productService.delete(incorrectProductId());

            Mockito.verify(repository).deleteById(incorrectProductId());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameProductServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultProduct()));
            Mockito.when(repository.save(any()))
                    .thenReturn((defaultProductWithName("newName")));

            productService = new ProductService(repository);
            productService.rename(defaultProductId(), "newName");

            Mockito.verify(repository).findById(defaultProductId());
            Mockito.verify(repository).save(defaultProductWithName("newName"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultNewProduct()));

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(defaultProductId(), "new . Name"))
                    .isInstanceOf(ProductIllegalNameException.class);

            Mockito.verify(repository).findById(defaultProductId());
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(nonExistingProductId(), "newName"))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(nonExistingProductId());
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(incorrectProductId(), "newName"))
                    .isInstanceOf(NoSuchProductException.class);

            Mockito.verify(repository).findById(incorrectProductId());
        }
    }
}