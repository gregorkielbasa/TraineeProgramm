package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.CustomerException;
import org.lager.model.Customer;

@DisplayName("CustomerService")
class CustomerServiceTest implements WithAssertions {

    @Test
    @DisplayName("when is empty returns an empty list")
    void getAllEmpty() {
        CustomerService CustomerService = new CustomerService();
        assertThat(CustomerService.getAll()).isEmpty();
    }

    @Nested
    @DisplayName("when tries to create")
    class InsertCustomerServiceTest {

        CustomerService CustomerService;

        @BeforeEach
        void init() {
            CustomerService = new CustomerService();

            CustomerService.create(new String("testOne"));
        }

        @Test
        @DisplayName("a new customer should have 2 customers")
        void newOne() {
            CustomerService.create("testTwo");

            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("a customer with existing name should have 2 customers")
        void sameName() {
            CustomerService.create("testOne");

            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testOne")
            );
        }

        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            assertThatThrownBy(() -> CustomerService.create(null))
                    .isInstanceOf(CustomerException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            assertThatThrownBy(() -> CustomerService.create("Test!!§$%&/()=Test"))
                    .isInstanceOf(CustomerException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchCustomerServiceTest {

        CustomerService CustomerService;

        @BeforeEach
        void init() {
            CustomerService = new CustomerService();

            CustomerService.create(new String("testOne"));
            CustomerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(CustomerService.search(100_000_000)).isEqualTo(
                    new Customer(100_000_000, "testOne")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(CustomerService.search(999_999_999)).isNull();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(CustomerService.search(1)).isNull();
        }
    }

    @Nested
    @DisplayName("when tries to remove")
    class RemoveCustomerServiceTest {

        CustomerService CustomerService;

        @BeforeEach
        void init() {
            CustomerService = new CustomerService();

            CustomerService.create(new String("testOne"));
            CustomerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(CustomerService.remove(100_000_000)).isEqualTo(
                    new Customer(100_000_000, "testOne"));
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(CustomerService.remove(999_999_999)).isNull();
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(CustomerService.remove(1)).isNull();
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }
    }

    @Nested
    @DisplayName("when tries to rename")
    class RenameCustomerServiceTest {

        CustomerService CustomerService;

        @BeforeEach
        void init() {
            CustomerService = new CustomerService();

            CustomerService.create(new String("testOne"));
            CustomerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            assertThat(CustomerService.rename(100_000_000, "newName")).isEqualTo(
                    new Customer(100_000_000, "newName"));
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "newName"),
                    new Customer(100_000_001, "testTwo"));
        }

        @Test
        @DisplayName("existing one with a new invalid name")
        void invalidNameExistingID() {
            assertThat(CustomerService.rename(100_000_000, "newName")).isEqualTo(
                    new Customer(100_000_000, "newName"));
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "newName"),
                    new Customer(100_000_001, "testTwo"));
        }

        @Test
        @DisplayName("non-existing one with a new proper name")
        void nonExistingID() {
            assertThat(CustomerService.rename(999_999_999, "newName")).isNull();
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("invalid ID with a new proper name")
        void invalidID() {
            assertThat(CustomerService.rename(1, "newName")).isNull();
            assertThat(CustomerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }
    }
}