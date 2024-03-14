package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.ProductException;
import org.lager.model.Product;

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
                    .isInstanceOf(ProductException.class);
        }

        @Test
        @DisplayName("a product with invalid Name should throw an exception")
        void invalidName() {
            assertThatThrownBy(() -> productService.insert("Test!!§$%&/()=Test"))
                    .isInstanceOf(ProductException.class);
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
                    new Product(100_000_000, "test1")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(productService.search(999_999_999)).isNull();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(productService.search(1)).isNull();
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
            assertThat(productService.remove(100_000_000)).isEqualTo(
                    new Product(100_000_000, "test1"));
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(productService.remove(999_999_999)).isNull();
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(productService.remove(1)).isNull();
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }
    }

    @Nested
    @DisplayName("")
    class RenameProductServiceTest {

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
            assertThat(productService.rename(100_000_000, "new test1")).isEqualTo(
                    new Product(100_000_000, "new test1"));
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "new test1"),
                    new Product(100_000_001, "test2"));
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(productService.rename(999_999_999, "some")).isNull();
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(productService.rename(1, "some")).isNull();
            assertThat(productService.getAll()).containsExactlyInAnyOrder(
                    new Product(100_000_000, "test1"),
                    new Product(100_000_001, "test2")
            );
        }
    }
}