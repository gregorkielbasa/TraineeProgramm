package org.lager.model;

import org.junit.jupiter.api.*;
import org.lager.exception.BasketException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Basket")
class BasketTest {

    Basket basket;

    @DisplayName("when empty")
    @Nested
    class BasketTestEmpty {

        @BeforeEach
        void init() {
            basket = new Basket();
        }

        @Test
        @DisplayName("should return an empty map")
        void getAllEmpty() {
            Map<Product, Integer> expected = new HashMap<>();
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should add and return two positions")
        void insert() {
            Product product1 = new Product("test1");
            Product product2 = new Product("test2");

            Map<Product, Integer> expected = Map.of(product1, 1, product2, 2);

            basket.insert(product1, 1);
            basket.insert(product2, 2);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should remove nothing (Non-existing) and return an empty map")
        void removeNonExisting() {
            Product nonExisting = new Product("Non Existing");

            Map<Product, Integer> expected = new HashMap<>();
            basket.remove(nonExisting);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }
    }

    @DisplayName("contains 5 elements")
    @Nested
    class BasketTestNotEmpty {

        Map<Product, Integer> expected;

        @BeforeEach
        void init() {
            Product product1 = new Product("test1");
            Product product2 = new Product("test2");
            Product product3 = new Product("test3");
            Product product4 = new Product("test4");
            Product product5 = new Product("test5");

            basket = new Basket();
            basket.insert(product1, 1);
            basket.insert(product2, 2);
            basket.insert(product3, 3);
            basket.insert(product4, 4);
            basket.insert(product5, 5);

            expected = new HashMap<>();
            expected.put(product1, 1);
            expected.put(product4, 4);
            expected.put(product3, 3);
            expected.put(product5, 5);
            expected.put(product2, 2);
        }

        @Test
        @DisplayName("should add 6th one")
        void insertNewOne() {
            Product product6 = new Product("test");

            expected.put(product6, 6);
            basket.insert(product6, 6);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should add amount of existing one")
        void insertExisting() {
            Product product = new Product("test5");

            expected.put(product, 10);
            basket.insert(product, 5);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should add negative amount and remove")
        void insertNegativeAmount() {
            Product product = new Product("test5");

            expected.remove(product);
            basket.insert(product, -5);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should do nothing when amount=0")
        void insertZeroAmount() {
            Product product = new Product("test5");

            basket.insert(product, 0);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }
        @Test
        @DisplayName("should remove existing one")
        void removeExisting() {
            Product product = new Product("test5");

            expected.remove(product);
            basket.remove(product);
            Map<Product, Integer> actual = basket.getAll();

            assertTrue(expected.entrySet().containsAll(actual.entrySet()));
            assertTrue(expected.values().containsAll(actual.values()));
            assertEquals(expected.size(), actual.size());
        }

        @Test
        @DisplayName("should return amount of existing one")
        void getAmountOfExisting() {
            int actual = basket.getAmountOf(new Product("test5"));

            assertEquals(5, actual);
        }

        @Test
        @DisplayName("should return 0 as an amount of Non-existing one")
        void getAmountOfNonExisting() {
            int actual = basket.getAmountOf(new Product("test100"));

            assertEquals(0, actual);
        }
    }

    @DisplayName("throws an exception")
    @Nested
    class BasketTestExceptions {

        @BeforeEach
        void init() {
            basket = new Basket();

            basket.insert(new Product("test"), 1);
        }

        @Test
        @DisplayName("when tries to insert NULL")
        void insert() {
            assertThrows(BasketException.class, () -> {
                basket.insert(null, 1);
            });
        }

        @Test
        @DisplayName("when tries to remove NULL")
        void remove() {
            assertThrows(BasketException.class, () -> {
                basket.remove(null);
            });
        }

        @Test
        @DisplayName("when tries to get Amount of NULL")
        void getAmountOf() {
            assertThrows(BasketException.class, () -> {
                basket.getAmountOf(null);
            });
        }
    }

    @DisplayName("when concats with")
    @Nested
    class BasketTestConcat {

        @BeforeEach
        void init() {
            basket = new Basket();

            basket.insert(new Product("test"), 1);
        }

        @Test
        @DisplayName("when tries to insert NULL")
        void insert() {
            assertThrows(BasketException.class, () -> {
                basket.insert(null, 1);
            });
        }

        @Test
        @DisplayName("when tries to remove NULL")
        void remove() {
            assertThrows(BasketException.class, () -> {
                basket.remove(null);
            });
        }

        @Test
        @DisplayName("when tries to get Amount of NULL")
        void getAmountOf() {
            assertThrows(BasketException.class, () -> {
                basket.getAmountOf(null);
            });
        }
    }
}