package org.lager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product")
class ProductTest {

    @Nested
    @DisplayName("throws Exception when")
    class ExceptionWhen {

        @Test
        @DisplayName("instantiated with NULL name")
        void ProductNull() {
            assertThrows(Exception.class, () -> {
                Product product = new Product(null);
            });
        }

        @Test
        @DisplayName("instantiated with empty name")
        void ProductEmpty() {
            assertThrows(Exception.class, () -> {
                Product product = new Product("");
            });
        }
    }

    @Test
    @DisplayName("with a proper name")
    void getName() {
        String expectedName = "proper name";
        Product product = new Product("proper name");
        String result = product.getName();

        assertEquals(expectedName, result);
    }

    @Test
    @DisplayName("is Equal")
    void testEquals() {
        Product product1 = new Product(new String("produkt name"));
        Product product2 = new Product(new String("produkt name"));

        assertEquals(product1, product2);
    }

    @Test
    @DisplayName("is NOT Equal")
    void testNotEquals() {
        Product product1 = new Product(new String("produkt name1"));
        Product product2 = new Product(new String("produkt name2"));

        assertNotEquals(product1, product2);
    }
}