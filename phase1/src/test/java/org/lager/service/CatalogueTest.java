package org.lager.service;

import org.junit.jupiter.api.*;
import org.lager.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Catalogue")
class CatalogueTest {

    Catalogue catalogue;

    @BeforeEach
    void init() {
        catalogue = new Catalogue();
    }

    @Nested
    @DisplayName("when inserts")
    class insert {
        @Test
        @DisplayName("one Product")
        void insertOne() {
            Product product = new Product("test");

            List<Product> expected = Arrays.asList(product);
            List<Product> actual = catalogue.insert(product);

            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("many various Product")
        void insertMany() {
            Product product1 = new Product("test1");
            Product product2 = new Product("test2");
            Product product3 = new Product("test3");

            catalogue.insert(product1);
            catalogue.insert(product2);
            List<Product> actual = catalogue.insert(product3);
            List<Product> expected = Arrays.asList(product1, product2, product3);

            //assertEquals(expected, actual);
            //assertArrayEquals(expected.toArray(), actual.toArray());
            //assertEquals(Collections.sort(expected), Collections.sort(actual));
            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("NULL Product throws an exception")
        void insertNull() {
            assertThrows(Exception.class, () -> {
                catalogue.insert(null);
            });
        }

        @Test
        @DisplayName("two the same Products throws an exception")
        void insertTwoSame() {
            Product product1 = new Product(new String("test"));
            Product product2 = new Product(new String("test"));

            catalogue.insert(product1);
            assertThrows(Exception.class, () -> {
                catalogue.insert(product2);
            });
        }
    }

    @Nested
    @DisplayName("when searches for")
    class search {
        @Test
        @DisplayName("existing Product")
        void searchExisting() {
            Product expected = new Product("test");

            catalogue.insert(expected);
            Product actual = catalogue.search("test");

            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("NON-existing Product returns NULL")
        void searchNonExisting() {
            Product actual = catalogue.search("test");

            assertEquals(null, actual);
        }

        @Test
        @DisplayName("NULL Product returns NULL")
        void searchNull() {
            Product actual = catalogue.search(null);

            assertEquals(null, actual);
        }
    }

    @Nested
    @DisplayName("when removes")
    class remove {
        @Test
        @DisplayName("one of various Product")
        void removeOne() {
            Product product1 = new Product("test1");
            Product product2 = new Product("test2");
            Product product3 = new Product("test3");
            catalogue.insert(product1);
            catalogue.insert(product2);
            catalogue.insert(product3);
            List<Product> actual = catalogue.remove("test2");
            List<Product> expected = Arrays.asList(product1, product3);

            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("last Product")
        void removeLast() {
            Product product = new Product("test");
            catalogue.insert(product);
            List<Product> actual = catalogue.remove("test");
            List<Product> expected = new ArrayList<>();

            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("non-existing Product")
        void removeNonExisting() {
            Product product = new Product("test");
            catalogue.insert(product);
            List<Product> actual = catalogue.remove("non-existing");
            List<Product> expected = Arrays.asList(product);

            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("non-existing Product from an empty Catalogue")
        void removeNonExistingFromEmpty() {
            List<Product> actual = catalogue.remove("non-existing");
            List<Product> expected = new ArrayList<>();

            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("NULL Product")
        void removeNull() {
            List<Product> actual = catalogue.remove(null);
            List<Product> expected = new ArrayList<>();

            assertTrue(expected.containsAll(actual));
            assertEquals(expected.size(), actual.size());
        }
    }
}