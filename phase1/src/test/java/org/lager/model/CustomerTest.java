//package org.lager.model;
//
//import org.junit.jupiter.api.*;
//import org.lager.exception.CustomerException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("Customer")
//class CustomerTest {
//
//    @Nested
//    @DisplayName("throws Exception when is created")
//    class CustomerTestException {
//
//        @Test
//        @DisplayName("with NULL name")
//        void nullName() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer(null, 200_000_000);
//            });
//        }
//
//        @Test
//        @DisplayName("with too short name")
//        void shortName() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("ab", 200_000_000);
//            });
//        }
//
//        @Test
//        @DisplayName("with too long name")
//        void longName() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("abcabcabcabcabcab", 200_000_000); //17 letters
//            });
//        }
//
//        @Test
//        @DisplayName("with a name with digits")
//        void nameWithDigits() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("abc123abc", 200_000_000);
//            });
//        }
//
//        @Test
//        @DisplayName("with a name with symbols")
//        void nameWithWhiteSymbols() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("abcabc ", 200_000_000);
//            });
//        }
//
//        @Test
//        @DisplayName("with a name with too short customer-number")
//        void shortNumber() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("test", 99_999_999);
//            });
//        }
//
//        @Test
//        @DisplayName("with a name with too long customer-number")
//        void longNumber() {
//            assertThrows(CustomerException.class, () -> {
//                Customer customer = new Customer("test", 1_000_000_000);
//            });
//        }
//    }
//
//    @Nested
//    @DisplayName("when created with proper name and number")
//    class CustomerTestProperConstructor {
//
//        Customer customer;
//
//        @BeforeEach
//        void init() {
//            customer = new Customer("TestABCabc", 123_123_123);
//        }
//
//        @Test
//        @DisplayName("returns name and number")
//        void checkNameAndNumber() {
//            long actualNumber = customer.getNumber();
//            String actualName = customer.getName();
//
//            assertEquals("TestABCabc", actualName);
//            assertEquals(123_123_123, actualNumber);
//        }
//
//        @Test
//        @DisplayName("should allow to change name")
//        void setName() {
//            String expected = new String("newName");
//            customer.setName("newName");
//            String actual = customer.getName();
//
//            assertEquals(expected, actual);
//        }
//    }
//
//    @Nested
//    @DisplayName("compares its HashCode to")
//    class CustomerTestHashCode {
//
//        Customer customer1;
//
//        @BeforeEach
//        void init() {
//            customer1 = new Customer("Test", 123_123_123);
//        }
//
//        @Test
//        @DisplayName("the same Customer")
//        void testHashCode() {
//            Customer customer2 = new Customer("Test", 123_123_123);
//
//            int expected = customer2.hashCode();
//            int actual = customer1.hashCode();
//
//            assertEquals(expected, actual);
//        }
//
//        @Test
//        @DisplayName("a Customer with a different costumer-number")
//        void testHashCodeNumber() {
//            Customer customer2 = new Customer("Test", 321_321_321);
//
//            int expected = customer2.hashCode();
//            int actual = customer1.hashCode();
//
//            assertNotEquals(expected, actual);
//        }
//
//        @Test
//        @DisplayName("a Customer with a different name")
//        void testHashCodeName() {
//            Customer customer2 = new Customer("DifferentName", 123_123_123);
//
//            int expected = customer2.hashCode();
//            int actual = customer1.hashCode();
//
//            assertNotEquals(expected, actual);
//        }
//    }
//
//    @Nested
//    @DisplayName("compares its Equality to")
//    class CustomerTestEquals {
//        Customer customer1;
//
//        @BeforeEach
//        void init() {
//            customer1 = new Customer("Test", 123_123_123);
//        }
//
//        @DisplayName("the same Customer")
//        void testEquals() {
//            Customer customer2 = new Customer("Test", 123_123_123);
//
//            assertTrue(customer1.equals(customer2));
//        }
//
//        @Test
//        @DisplayName("a Customer with a different costumer-number")
//        void testEqualsNumber() {
//            Customer customer2 = new Customer("Test", 321_321_321);
//
//            assertFalse(customer1.equals(customer2));
//        }
//
//        @Test
//        @DisplayName("a Customer with a different name")
//        void testEqualsName() {
//            Customer customer2 = new Customer("DifferentName", 123_123_123);
//
//            assertFalse(customer1.equals(customer2));
//        }
//
//        @Test
//        @DisplayName("a NULL Customer")
//        void testEqualsNull() {
//            Customer customer2 = null;
//
//            assertFalse(customer1.equals(customer2));
//        }
//
//        @Test
//        @DisplayName("a different-class Customer")
//        void testEqualsDifferentClass() {
//            String customer2 = new String("test");
//
//            assertFalse(customer1.equals(customer2));
//        }
//    }
//}