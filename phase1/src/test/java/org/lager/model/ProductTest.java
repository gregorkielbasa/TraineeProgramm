package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.ProductIllegalNumberException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
                assertThatThrownBy(() -> new Product(123_123_123, null))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("empty")
            void emptyName() {
                assertThatThrownBy(() -> new Product(123_123_123, ""))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Product(123_123_123, "na"))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() { //25 characters
                assertThatThrownBy(() -> new Product(123_123_123, "namenamenamenamenamenamen"))
                        .isInstanceOf(ProductIllegalNameException.class);
            }

            @Test
            @DisplayName("contains illegal character")
            void illegalCharacterName() { //illegal '.'
                assertThatThrownBy(() -> new Product(123_123_123, "name123.321name"))
                        .isInstanceOf(ProductIllegalNameException.class);
            }
        }

        @Nested
        @DisplayName("number")
        class ProductNumberTestException {

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Product(123_123_12, "name"))
                        .isInstanceOf(ProductIllegalNumberException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() {
                assertThatThrownBy(() -> new Product(123_123_123_1, "name"))
                        .isInstanceOf(ProductIllegalNumberException.class);
            }
        }
    }

    @Test
    @DisplayName("with a proper name and number")
    void getNameAndNumber() {
        Product product = new Product(123_123_123, "proper name");

        assertThat(product.getNumber()).isEqualTo(123_123_123L);
        assertThat(product.getName()).isEqualTo("proper name");
    }

    @Nested
    @DisplayName("when tries to change name")
    class ProductSetNameTest {

        Product product;

        @BeforeEach
        void init() {
            product = new Product(123_123_123L, "name");
        }

        @Test
        @DisplayName("works with a proper name")
        void properCase() {
            product.setName("new name");
            assertThat(product.getName()).isEqualTo("new name");
        }

        @Test
        @DisplayName("throws an exception with too long name")
        void longName() {  //25 characters
            assertThatThrownBy(() -> product.setName("namenamenamenamenamenamen"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("throws an exception when name contains illegal character")
        void illegalCharacterName() {  //illegal '.'
            assertThatThrownBy(() -> product.setName("name123.321name"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }
    }

    @Test
    @DisplayName("when converts into CSV Record")
    void toCsvRecord() {
        Product product = new Product(123_123_123, "properName");

        assertThat(product.toCsvRecord()).isEqualTo("123123123,properName");
    }

    @Nested
    @DisplayName("when converts from CSV Record")
    class getFromCsvRecord {

        @Test
        @DisplayName("works with all correct parameters")
        void properCase() {
            String csvRecord = "123123123,productName";

            Optional<Product> result = Product.getFromCsvRecord(csvRecord);

            assertThat(result.get()).isEqualTo(new Product(123_123_123, "productName"));
        }

        @Test
        @DisplayName("returns an empty Optional when CSV Record is NULL")
        void csvRecordNull() {
            String csvRecord = null;

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when CSV Record contains incorrect number")
        void csvRecordIllegalNumber() {
            String csvRecord = "123abc123,productName";

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when name is empty")
        void csvRecordEmptyName() {
            String csvRecord = "123123123,";

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when theres no name")
        void csvRecordNoName() {
            String csvRecord = "123123123";

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when number is incorrect")
        void csvRecordIncorrectNumber() {
            String csvRecord = "123,productName";

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when name is incorrect")
        void csvRecordIncorrectName() {
            String csvRecord = "123123123,product§$%&(Name";

            assertThat(Product.getFromCsvRecord(csvRecord)).isEmpty();
        }
    }

    @Nested
    @DisplayName("is equal")
    class ProductTestEquals {

        Product product1 = new Product(123_123_123L, new String("product name"));

        @Test
        @DisplayName("is Equal when its exactly the same Product")
        void testEqualsExactlySame() {
            assertTrue(product1.equals(product1));
        }

        @Test
        @DisplayName("when has the same properties")
        void testEquals() {
            Product product2 = new Product(123_123_123L, new String("product name"));

            assertTrue(product1.equals(product2));
        }
    }

    @Nested
    @DisplayName("is NOT equal")
    class ProductTestNotEquals {

        Product product1 = new Product(123_123_123L, new String("product name"));

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
            Product product2 = new Product(123_123_123L, new String("product different name"));

            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("when comparing to an object with a different number")
        void testEqualsNumber() {
            Product product2 = new Product(123_000_000L, new String("product name"));

            assertFalse(product1.equals(product2));
        }
    }

    @Nested
    @DisplayName("compares its HashCode to")
    class HashCodeTest {

        Product product1 = new Product(123_123_123L, new String("product name"));

        @Test
        @DisplayName("the same objects")
        void testHashCodeExactlySame() {
            assertThat(product1.hashCode()).isEqualTo(product1.hashCode());
        }

        @Test
        @DisplayName("an objects with the same properties")
        void testHashCodeEquals() {
            Product product2 = new Product(123_123_123L, "product name");

            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("an object with different Numbers")
        void testHashCodeDifferentID() {
            Product product2 = new Product(123_000_123L, "product name");

            assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("an object with different Names")
        void testHashCodeDifferentName() {
            Product product2 = new Product(123_123_123L, "product two");

            assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
        }

        @Test
        @DisplayName("to String")
        void testToString() {
            Product product = new Product(123_123_123L, "test");

            assertThat(product.toString()).isEqualTo("Product{number=123123123, name='test'}");
        }
    }
}