package org.lager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product")
class ProductTest {

    @Nested
    @DisplayName("throws Exception when")
    class ProductTestException {

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
        String expected = "proper name";
        Product product = new Product("proper name");
        String actual = product.getName();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("is Equal")
    void testEquals() {
        Product product1 = new Product(new String("product name"));
        Product product2 = new Product(new String("product name"));

        assertEquals(product1, product2);
    }

    @Nested
    @DisplayName("is NOT Equal")
    class ProductTestNotEquals {

        @Test
        @DisplayName("when comparing to NULL")
        void testNotEqualsNull() {
            Product product1 = new Product(new String("product name1"));
            Product product2 = null;

            assertNotEquals(product1, product2);
        }

        @Test
        @DisplayName("when comparing to an object")
        void testNotEqualsObject() {
            Product product1 = new Product(new String("product name1"));
            Object product2 = new Object();

            assertNotEquals(product1, product2);
        }

        @Test
        @DisplayName("when comparing two different objects")
        void testNotEquals() {
            Product product1 = new Product(new String("product name1"));
            Product product2 = new Product(new String("product name2"));

            assertNotEquals(product1, product2);
        }

    }

    @Test
    @DisplayName("hash code should be the same")
    void testHashCodeEquals() {
        Product product1 = new Product(new String("product name1"));
        Product product2 = new Product(new String("product name1"));

        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("hash code should be diffrent")
    void testHashCodeNotEquals() {
        Product product1 = new Product(new String("product name1"));
        Product product2 = new Product(new String("product name2"));

        assertNotEquals(product1, product2);
    }

    @Test
    @DisplayName("to String")
    void testToString() {
        String expected = "Product{name='test'}";
        Product product = new Product(new String("test"));
        String actual = product.toString();

        assertEquals(expected, actual);
    }
}