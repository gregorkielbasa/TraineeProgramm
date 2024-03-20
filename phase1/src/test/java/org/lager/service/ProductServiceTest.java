package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.NoSuchProductException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.Product;

import java.util.Optional;

@DisplayName("ProductService")
class ProductServiceTest implements WithAssertions {

    @Test
    @DisplayName("when is empty returns an empty list")
    void getAllEmpty() {
        ProductService productService = new ProductService();
        assertThat(productService.getAll()).isEmpty();
    }

    @Nested
    @DisplayName("when tries to insert")
    class InsertProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() {
            productService = new ProductService();

            productService.insert(new String("test1"));
        }

        @Test
        @DisplayName("a new product should have 2 products")
        void newOne() {
            productService.insert("test2");

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("a product with existing name should have 2 products")
        void sameName() {
            productService.insert("test1");

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test1")
            );
        }

        @Test
        @DisplayName("a product with null Name should throw an exception")
        void nullName() {
            assertThatThrownBy(() -> productService.insert(null))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product with invalid Name should throw an exception")
        void invalidName() {
            assertThatThrownBy(() -> productService.insert("Test!!§$%&/()=Test"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() {
            productService = new ProductService();

            productService.insert(new String("test1"));
            productService.insert(new String("test2"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(productService.search(100_000_000)).isEqualTo(
                    Optional.of(new Product(100_000_000, "test1"))
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(productService.search(999_999_999)).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(productService.search(1)).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() {
            productService = new ProductService();

            productService.insert(new String("test1"));
            productService.insert(new String("test2"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(productService.validatePresence(100_000_000)).isTrue();
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThatThrownBy(() -> productService.validatePresence(999_999_999))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThatThrownBy(() -> productService.validatePresence(1))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }

    @Nested
    @DisplayName("when tries to remove")
    class RemoveProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() {
            productService = new ProductService();

            productService.insert(new String("test1"));
            productService.insert(new String("test2"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            productService.remove(100_000_000);

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            productService.remove(999_999_999);

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            productService.remove(1);

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() {
            productService = new ProductService();

            productService.insert(new String("test1"));
            productService.insert(new String("test2"));
        }

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            productService.rename(100_000_000, "new test1");

            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "new test1"),
                    new Product(100_000_001, "test2"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            assertThatThrownBy(() -> productService.rename(100_000_000, "new %%&(%$§test1"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            assertThatThrownBy(() -> productService.rename(999_999_999, "some"))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            assertThatThrownBy(() -> productService.rename(1, "some"))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }
}