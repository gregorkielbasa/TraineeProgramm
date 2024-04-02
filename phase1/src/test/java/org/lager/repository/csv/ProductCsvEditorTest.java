package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.ProductCsvNullException;
import org.lager.model.Product;
import org.lager.repository.csv.ProductCsvEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Product CSV Editor")
class ProductCsvEditorTest implements WithAssertions {

    @Test
    @DisplayName("throws an exception when loads non existing CSV file")
    void loadNonExisting() {
        ProductCsvEditor csvEditor = new ProductCsvEditor("nonExistingFile.csv", "header");

        assertThatThrownBy(csvEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("throws an exception when saves NULL List")
    void saveNull() {
        ProductCsvEditor csvEditor = new ProductCsvEditor("any.csv", "header");

        assertThatThrownBy(() -> csvEditor.saveToFile(null))
                .isInstanceOf(ProductCsvNullException.class);
    }

    @Nested
    @DisplayName("writes and loads")
    class ProductCsvWorks {
        private final Product PRODUCT_1 = new Product(123_123_123, "properName");
        private final Product PRODUCT_2 = new Product(123_456_789, "otherName");
        private ProductCsvEditor csvEditor;
        private List<Product> products;

        @BeforeEach
        void init() {
            csvEditor = new ProductCsvEditor("test.csv", "number,name");
            products = new ArrayList<>();
        }

        @Test
        @DisplayName("empty list")
        void emptyList() throws IOException {
            csvEditor.saveToFile(products);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of());
        }

        @Test
        @DisplayName("list with one Product")
        void listWithOneProduct() throws IOException {
            products.add(PRODUCT_1);

            csvEditor.saveToFile(products);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1));
        }

        @Test
        @DisplayName("list with two Product")
        void listWithTwoProduct() throws IOException {
            products.add(PRODUCT_1);
            products.add(PRODUCT_2);

            csvEditor.saveToFile(products);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }
    }
}