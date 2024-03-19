package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;

import java.util.Optional;

@DisplayName("customerService")
class CustomerServiceTest implements WithAssertions {

    @Test
    @DisplayName("when is empty returns an empty list")
    void getAllEmpty() {
        CustomerService customerService = new CustomerService();
        assertThat(customerService.getAll()).isEmpty();
    }

    @Nested
    @DisplayName("when tries to create")
    class InsertCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() {
            customerService = new CustomerService();

            customerService.create(new String("testOne"));
        }

        @Test
        @DisplayName("a new customer should have 2 customers")
        void newOne() {
            customerService.create("testTwo");

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("a customer with existing name should have 2 customers")
        void sameName() {
            customerService.create("testOne");

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testOne")
            );
        }

        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            assertThatThrownBy(() -> customerService.create("Test!!§$%&/()=Test"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() {
            customerService = new CustomerService();

            customerService.create(new String("testOne"));
            customerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(customerService.search(100_000_000)).isEqualTo(
                    Optional.of(new Customer(100_000_000, "testOne"))
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(customerService.search(999_999_999)).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(customerService.search(1)).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() {
            customerService = new CustomerService();

            customerService.create(new String("testOne"));
            customerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(customerService.validatePresence(100_000_000)).isTrue();
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThatThrownBy(() -> customerService.validatePresence(999_999_999))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThatThrownBy(() -> customerService.validatePresence(1))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }

    @Nested
    @DisplayName("when tries to remove")
    class RemoveCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() {
            customerService = new CustomerService();

            customerService.create(new String("testOne"));
            customerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            customerService.remove(100_000_000);

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            customerService.remove(999_999_999);

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            customerService.remove(1);

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "testOne"),
                    new Customer(100_000_001, "testTwo")
            );
        }
    }

    @Nested
    @DisplayName("when tries to rename")
    class RenameCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() {
            customerService = new CustomerService();

            customerService.create(new String("testOne"));
            customerService.create(new String("testTwo"));
        }

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            customerService.rename(100_000_000, "newName");

            assertThat(customerService.getAll()).containsExactlyInAnyOrder(
                    new Customer(100_000_000, "newName"),
                    new Customer(100_000_001, "testTwo"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            assertThatThrownBy(() -> customerService.rename(100_000_000, "new . Name"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            assertThatThrownBy(() -> customerService.rename(999_999_999, "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            assertThatThrownBy(() -> customerService.rename(1, "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }
}