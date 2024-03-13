//package org.lager.service;
//
//import org.junit.jupiter.api.*;
//import org.lager.exception.ProductException;
//import org.lager.exception.ProductServiceException;
//import org.lager.model.Product;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("ProductService")
//class ProductServiceTest {
//
//    ProductService productService;
//
//    @BeforeEach
//    void init() {
//        productService = new ProductService();
//    }
//
//    @Nested
//    @DisplayName("when inserts")
//    class insert {
//        @Test
//        @DisplayName("one Product")
//        void insertOne() {
//            Product product = new Product("test");
//            productService.insert("test");
//
//            List<Product> expected = Arrays.asList(product);
//            List<Product> actual = productService.getAll();
//
//            assertEquals(expected, actual);
//        }
//
//        @Test
//        @DisplayName("many various Product")
//        void insertMany() {
//            Product product1 = new Product("test1");
//            Product product2 = new Product("test2");
//            Product product3 = new Product("test3");
//
//            productService.insert("test1");
//            productService.insert("test2");
//            productService.insert("test3");
//
//            List<Product> actual = productService.getAll();
//            List<Product> expected = Arrays.asList(product1, product2, product3);
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//
//        @Test
//        @DisplayName("NULL Product throws an exception")
//        void insertNull() {
//            assertThrows(ProductException.class, () -> {
//                productService.insert(null);
//            });
//        }
//
//        @Test
//        @DisplayName("two the same Products throws an exception")
//        void insertTwoSame() {
//
//            productService.insert("test");
//            assertThrows(ProductServiceException.class, () -> {
//                productService.insert("test");
//            });
//        }
//    }
//
//    @Nested
//    @DisplayName("when searches for")
//    class search {
//        @Test
//        @DisplayName("existing Product")
//        void searchExisting() {
//            productService.insert("test");
//            Product expected = new Product("test");
//            Product actual = productService.search("test");
//
//            assertEquals(expected, actual);
//        }
//
//        @Test
//        @DisplayName("NON-existing Product returns NULL")
//        void searchNonExisting() {
//            Product actual = productService.search("test");
//
//            assertEquals(null, actual);
//        }
//
//        @Test
//        @DisplayName("NULL Product returns NULL")
//        void searchNull() {
//            Product actual = productService.search(null);
//
//            assertEquals(null, actual);
//        }
//    }
//
//    @Nested
//    @DisplayName("when removes")
//    class remove {
//        @Test
//        @DisplayName("one of various Product")
//        void removeOne() {
//            Product product1 = new Product("test1");
//            Product product2 = new Product("test2");
//            Product product3 = new Product("test3");
//
//            productService.insert("test1");
//            productService.insert("test2");
//            productService.insert("test3");
//            productService.remove("test2");
//
//            List<Product> actual = productService.getAll();
//            List<Product> expected = Arrays.asList(product1, product3);
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//
//        @Test
//        @DisplayName("last Product")
//        void removeLast() {
//            Product product = new Product("test");
//            productService.insert("test");
//            productService.remove("test");
//
//            List<Product> actual = productService.getAll();
//            List<Product> expected = new ArrayList<>();
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//
//        @Test
//        @DisplayName("non-existing Product")
//        void removeNonExisting() {
//            Product product = new Product("test");
//            productService.insert("test");
//            productService.remove("non-existing");
//
//            List<Product> actual = productService.getAll();
//            List<Product> expected = Arrays.asList(product);
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//
//        @Test
//        @DisplayName("non-existing Product from an empty Catalogue")
//        void removeNonExistingFromEmpty() {
//            productService.remove("non-existing");
//            List<Product> actual = productService.getAll();
//            List<Product> expected = new ArrayList<>();
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//
//        @Test
//        @DisplayName("NULL Product")
//        void removeNull() {
//            productService.remove(null);
//            List<Product> actual = productService.getAll();
//            List<Product> expected = new ArrayList<>();
//
//            assertTrue(expected.containsAll(actual));
//            assertEquals(expected.size(), actual.size());
//        }
//    }
//}