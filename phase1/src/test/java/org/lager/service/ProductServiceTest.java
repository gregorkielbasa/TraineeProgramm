package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.NoSuchProductException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.RepositoryException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.ProductFixtures.*;

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
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.doNothing().when(repository).save(Mockito.any());

            productService = new ProductService(repository);
            Product newProduct = productService.create(defaultName());

            assertThat(newProduct).isEqualTo(defaultProduct());
            Mockito.verify(repository).save(defaultProduct());
        }


        @Test
        @DisplayName("a product with null Name should throw an exception")
        void nullName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            productService = new ProductService(repository);

            assertThatThrownBy(() -> productService.create(null))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product with invalid Name should throw an exception")
        void invalidName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            productService = new ProductService(repository);

            assertThatThrownBy(() -> productService.create(incorrectName()))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product but repository cannot save")
        void repositoryException() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.doThrow(new RepositoryException("any"))
                    .when(repository).save(Mockito.any());

            productService = new ProductService(repository);

            assertThatThrownBy(() -> productService.create(defaultName()))
                    .isInstanceOf(RepositoryException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultProduct()));

            productService = new ProductService(repository);
            assertThat(productService.search(defaultNumber())).isEqualTo(
                    Optional.of(defaultProduct())
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThat(productService.search(defaultNumber())).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThat(productService.search(incorrectNumber())).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultProduct()));

            productService = new ProductService(repository);
            productService.validatePresence(defaultNumber());

            Mockito.verify(repository).read(defaultNumber());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.validatePresence(defaultNumber()))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.validatePresence(incorrectNumber()))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }

    @Nested
    @DisplayName("when deletes")
    class RemoveProductServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.doNothing().when(repository).delete(Mockito.anyLong());

            productService = new ProductService(repository);
            productService.delete(defaultNumber());

            Mockito.verify(repository).delete(defaultNumber());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).delete(incorrectNumber());

            productService = new ProductService(repository);
            productService.delete(incorrectNumber());

            Mockito.verify(repository).delete(incorrectNumber());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameProductServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.when(repository.read(defaultNumber())).thenReturn(Optional.of(defaultProduct()));
            Mockito.doNothing().when(repository).save(Mockito.any());

            productService = new ProductService(repository);
            productService.rename(defaultNumber(), "newName");

            Mockito.verify(repository).save(productWithName("newName"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            Mockito.when(repository.read(defaultNumber())).thenReturn(Optional.of(defaultProduct()));

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(defaultNumber(), "new . Name"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            Mockito.when(repository.read(nonExistingNumber())).thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(nonExistingNumber(), "newName"))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber())).thenReturn(Optional.empty());

            productService = new ProductService(repository);
            assertThatThrownBy(() -> productService.rename(incorrectNumber(), "newName"))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }
}