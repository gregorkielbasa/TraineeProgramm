package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.CustomerIllegalNumberException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer")
class CustomerTest implements WithAssertions {

    @Nested
    @DisplayName("throws Exception when is created with")
    class CustomerTestException {

        @Nested
        @DisplayName("name")
        class CustomerNameTestException {

            @Test
            @DisplayName("NULL")
            void nullName() {
                assertThatThrownBy(() -> new Customer(123_123_123, null))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

            @Test
            @DisplayName("empty")
            void emptyName() {
                assertThatThrownBy(() -> new Customer(123_123_123, ""))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Customer(123_123_123, "na"))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() { //25 characters
                assertThatThrownBy(() -> new Customer(123_123_123, "namenamenamenamenamenamen"))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

            @Test
            @DisplayName("contains digits")
            void nameWithDigits() {
                assertThatThrownBy(() -> new Customer(123_123_123, "name123name"))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

            @Test
            @DisplayName("contains illegal character")
            void nameWithWhiteSymbols() { //illegal '.'
                assertThatThrownBy(() -> new Customer(123_123_123, "abc.abc"))
                        .isInstanceOf(CustomerIllegalNameException.class);
            }

        }
        @Nested
        @DisplayName("number")
        class CustomerNumberTestException {

            @Test
            @DisplayName("too short")
            void shortName() {
                assertThatThrownBy(() -> new Customer(123_123_12, "name"))
                        .isInstanceOf(CustomerIllegalNumberException.class);
            }

            @Test
            @DisplayName("too long")
            void longName() {
                assertThatThrownBy(() -> new Customer(123_123_123_1, "name"))
                        .isInstanceOf(CustomerIllegalNumberException.class);
            }

        }
    }
    @Test
    @DisplayName("with a proper name and number")
    void getNameAndNumber() {
        Customer customer = new Customer(123_123_123, "properName");

        assertThat(customer.getNumber()).isEqualTo(123_123_123L);
        assertThat(customer.getName()).isEqualTo("properName");
    }

    @Nested
    @DisplayName("when tries to change name")
    class CustomerSetNameTest {

        Customer customer;

        @BeforeEach
        void init() {
            customer = new Customer(123_123_123L, "TestABCabc");
        }

        @Test
        @DisplayName("works with a proper name")
        void properCase() {
            customer.setName("newName");
            assertThat(customer.getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("throws an exception with too long name")
        void longName() { //25 characters
            assertThatThrownBy(() -> customer.setName("namenamenamenamenamenamen"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("throws an exception when name contains illegal character")
        void illegalCharacterName() { //illegal '.'
            assertThatThrownBy(() -> customer.setName("name.name"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

    }
    @Test
    @DisplayName("when converts into CSV Record")
    void toCsvRecord() {
        Customer customer = new Customer(123_123_123, "properName");

        assertThat(customer.toCsvRecord()).isEqualTo("123123123,properName");
    }

    @Nested
    @DisplayName("when converts from CSV Record")
    class getFromCsvRecord {

        @Test
        @DisplayName("works with all correct parameters")
        void properCase() {
            String csvRecord = "123123123,customerName";

            Optional<Customer> result = Customer.getFromCsvRecord(csvRecord);

            assertThat(result.get()).isEqualTo(new Customer(123_123_123, "customerName"));
        }

        @Test
        @DisplayName("returns an empty Optional when CSV Record is NULL")
        void csvRecordNull() {
            String csvRecord = null;

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when CSV Record contains incorrect number")
        void csvRecordIllegalNumber() {
            String csvRecord = "123abc123,customerName";

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when name is empty")
        void csvRecordEmptyName() {
            String csvRecord = "123123123,";

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when theres no name")
        void csvRecordNoName() {
            String csvRecord = "123123123";

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when umber is incorrect")
        void csvRecordIncorrectNumber() {
            String csvRecord = "123,customerName";

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }

        @Test
        @DisplayName("returns an empty Optional when name is incorrect")
        void csvRecordIncorrectName() {
            String csvRecord = "123123123,customer§$%&(Name";

            assertThat(Customer.getFromCsvRecord(csvRecord)).isEmpty();
        }
    }

    @Nested
    @DisplayName("is equal")
    class CustomerTestEquals {


        Customer customer1 = new Customer(123_123_123L, new String("Test"));

        @Test
        @DisplayName("is Equal when its exactly the same Customer")
        void testEqualsExactlySame() {
            assertTrue(customer1.equals(customer1));
        }

        @Test
        @DisplayName("when has the same properties")
        void testEquals() {
            Customer customer2 = new Customer(123_123_123L,"Test");

            assertTrue(customer1.equals(customer2));
        }
    }
    @Nested
    @DisplayName("is NOT equal when comparing")
    class CustomerTestNotEquals {


        Customer customer1 = new Customer(123_123_123L, "Test");

        @Test
        @DisplayName("to NULL")
        void testEqualsNumber() {
            Customer customer2 = null;

            assertFalse(customer1.equals(customer2));
        }

        @Test
        @DisplayName("two different classes")
        void testEqualsDifferentClass() {
            String customer2 = new String("test");

            assertFalse(customer1.equals(customer2));
        }

        @Test
        @DisplayName("to an object with a different properties")
        void testEqualsName() {
            Customer customer2 = new Customer(123_123_123L, "DifferentTest");

            assertFalse(customer1.equals(customer2));
        }
    }
    @Nested
    @DisplayName("compares its HashCode to")
    class HashCodeTest {


        Customer customer1 = new Customer(123_123_123, new String("Test"));

        @Test
        @DisplayName("the same objects")
        void testHashCodeExactlySame() {
            assertThat(customer1.hashCode()).isEqualTo(customer1.hashCode());
        }

        @Test
        @DisplayName("an objects with the same properties")
        void testHashCodeEquals() {
            Customer customer2 = new Customer(123_123_123, "Test");

            assertThat(customer1.hashCode()).isEqualTo(customer2.hashCode());
        }

        @Test
        @DisplayName("an object with different Numbers")
        void testHashCodeDifferentNumber() {
            Customer customer2 = new Customer(123_000_123, "test");

            int expected = customer2.hashCode();
            int actual = customer1.hashCode();

            assertThat(customer1.hashCode()).isNotEqualTo(customer2.hashCode());
        }

        @Test
        @DisplayName("an object with different Names")
        void testHashCodeDifferentName() {
            Customer customer2 = new Customer(123_123_123, "DifferentTest");

            assertThat(customer1.hashCode()).isNotEqualTo(customer2.hashCode());
        }
    }

    @Test
    @DisplayName("to String")
    void testToString() {
        Customer customer = new Customer(123_123_123L, "test");

        assertThat(customer.toString()).isEqualTo("Customer{number=123123123, name='test'}");
    }
}