package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.ProductIllegalPriceException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Product")
class ProductTest implements WithAssertions {

    @Nested
    @DisplayName("throws Exception when is created with")
    class ProductTestException {

        @Nested
        @DisplayName("name")
        class ProductNameTestException {

            @Test
            @DisplayName("NULL")
            void nullName() {
                assertThatThrownBy(() -> new Product(null, 1.0))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("empty")
            void emptyName() {
                assertThatThrownBy(() -> new Product("", 1.0))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Product("na", 1.0))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() { //25 characters
                assertThatThrownBy(() -> new Product("namenamenamenamenamenamen", 1.0))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("contains illegal character")
            void illegalCharacterName() { //illegal '.'
                assertThatThrownBy(() -> new Product("name123.321name", 1.0))
                        .isInstanceOf(ProductIllegalNameException.class);
            }
        }

        @Nested
        @DisplayName("ID")
        class ProductIDTestException {

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Product(123_123_12, "name", 1.0))
                        .isInstanceOf(ProductIllegalIdException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() {
                assertThatThrownBy(() -> new Product(123_123_123_1, "name", 1.0))
                        .isInstanceOf(ProductIllegalIdException.class);
            }
        }

        @Test
        @DisplayName("price under zero")
        void productPriceUnderZero() {
                assertThatThrownBy(() -> new Product(123_123_123, "name", -123.45))
                        .isInstanceOf(ProductIllegalPriceException.class);
        }
    }

    @Test
    @DisplayName("with a proper name, price and ID")
    void getProductNameAndID() {
        Product product = new Product(123_123_123, "proper name", 1.0);

        assertThat(product.getProductId()).isEqualTo(123_123_123L);
        assertThat(product.getProductName()).isEqualTo("proper name");
        assertThat(product.getProductPrice()).isEqualTo(1.0);
    }

    @Nested
    @DisplayName("when tries to change name")
    class SetProductNameTest {

        Product product;

        @BeforeEach
        void init() {
            product = new Product("name", 1.0);
        }

        @Test
        @DisplayName("works with a proper name")
        void properCase() {
            product.setProductName("new name");
            assertThat(product.getProductName()).isEqualTo("new name");
        }

        @Test
        @DisplayName("throws an exception with too long name")
        void longName() {  //25 characters
            assertThatThrownBy(() -> product.setProductName("namenamenamenamenamenamen"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("throws an exception when name contains illegal character")
        void illegalCharacterName() {  //illegal '.'
            assertThatThrownBy(() -> product.setProductName("name123.321name"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }
    }

    @Nested
    @DisplayName("when tries to change price")
    class SetProductPriceTest {

        Product product;

        @BeforeEach
        void init() {
            product = new Product("name", 1.0);
        }

        @Test
        @DisplayName("works with a proper price")
        void properCase() {
            product.setProductPrice(2.5);
            assertThat(product.getProductPrice()).isEqualTo(2.5);
        }

        @Test
        @DisplayName("throws an exception with price under zero")
        void underZero() {  //25 characters
            assertThatThrownBy(() -> product.setProductPrice(-123.45))
                    .isInstanceOf(ProductIllegalPriceException.class);
        }
    }

    @Nested
    @DisplayName("is equal")
    class ProductTestEquals {

        Product product1 = new Product(123_123_123L, new String("product name"), 1.0);

        @Test
        @DisplayName("is Equal when its exactly the same Product")
        void testEqualsExactlySame() {
            assertTrue(product1.equals(product1));
        }

        @Test
        @DisplayName("when has the same properties")
        void testEquals() {
            Product product2 = new Product(123_123_123L, new String("product name"), 1.0);

            assertTrue(product1.equals(product2));
        }
    }

    @Nested
    @DisplayName("is NOT equal")
    class ProductTestNotEquals {

        Product product1 = new Product(123_123_123L, new String("product name"), 1.0);

        @Test
        @DisplayName("when comparing to NULL")
        void testNotEqualsNull() {
            Product product2 = null;

            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("when comparing two different classes")
        void testNotEquals() {
            String product2 = new String("test");

            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("when comparing to an object with a different name")
        void testEqualsName() {
            Product product2 = new Product(123_123_123L, new String("product different name"), 1.0);

            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("when comparing to an object with a different ID")
        void testEqualsID() {
            Product product2 = new Product(123_000_000L, new String("product name"), 1.0);

            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("when comparing to an object with a different price")
        void testEqualsPrice() {
            Product product2 = new Product(123_123_123L, new String("product name"), 2.0);

            assertFalse(product1.equals(product2));
        }
    }

    @Nested
    @DisplayName("compares its HashCode to")
    class HashCodeTest {

        Product product1 = new Product(123_123_123L, new String("product name"), 1.0);

        @Test
        @DisplayName("the same objects")
        void testHashCodeExactlySame() {
            assertThat(product1.hashCode()).isEqualTo(product1.hashCode());
        }

        @Test
        @DisplayName("an objects with the same properties")
        void testHashCodeEquals() {
            Product product2 = new Product(123_123_123L, "product name", 1.0);

            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("an object with different IDs")
        void testHashCodeDifferentID() {
            Product product2 = new Product(123_000_123L, "product name", 1.0);

            assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("an object with different Names")
        void testHashCodeDifferentName() {
            Product product2 = new Product(123_123_123L, "product two", 1.0);

            assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("an object with different IDs")
        void testHashCodeDifferentPrice() {
            Product product2 = new Product(123_123_123L, "product name", 2.0);

            assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
        }
    }

    @Test
    @DisplayName("to String")
    void testToString() {
        Product product = new Product(123_123_123L, "test", 1.0);

        assertThat(product.toString()).isEqualTo("Product{ID=123123123, productName='test', productPrice=1.0}");
    }

}