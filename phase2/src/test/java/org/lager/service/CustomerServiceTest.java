package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.dto.CustomerDto;
import org.lager.repository.BasketRepository;
import org.lager.repository.CustomerRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service")
class CustomerServiceTest implements WithAssertions {

    @Mock
    private CustomerRepository repository;
    @Mock
    private BasketRepository basketRepository;

    CustomerService customerService;

    @Nested
    @DisplayName("tests create() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one Customer")
        void properOne() {
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultCustomer());

            customerService = new CustomerService(repository, basketRepository);
            CustomerDto customer = customerService.create(defaultCustomerName());

            assertThat(customer).isEqualTo(new CustomerDto(defaultCustomer()));
            Mockito.verify(repository).save(defaultNewCustomer());
        }


        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            customerService = new CustomerService(repository, basketRepository);

            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            customerService = new CustomerService(repository, basketRepository);

            assertThatThrownBy(() -> customerService.create(incorrectCustomerName()))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository, basketRepository);
            CustomerDto customer = customerService.get(defaultCustomerId());

            assertThat(customer).isEqualTo(new CustomerDto(defaultCustomer()));
            Mockito.verify(repository).findById(defaultCustomerId());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(()->customerService.get(defaultCustomerId()))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(defaultCustomerId());
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(()->customerService.get(incorrectCustomerId()))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(incorrectCustomerId());
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository, basketRepository);
            customerService.validatePresence(defaultCustomerId());

            Mockito.verify(repository).findById(defaultCustomerId());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(() -> customerService.validatePresence(defaultCustomerId()))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(defaultCustomerId());
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(() -> customerService.validatePresence(incorrectCustomerId()))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(incorrectCustomerId());
        }
    }

    @Nested
    @DisplayName("when deletes")
    class RemoveCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.doNothing().when(repository).deleteById(anyLong());

            customerService = new CustomerService(repository, basketRepository);
            customerService.delete(defaultCustomerId());

            Mockito.verify(repository).deleteById(defaultCustomerId());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).deleteById(anyLong());

            customerService = new CustomerService(repository, basketRepository);
            customerService.delete(incorrectCustomerId());

            Mockito.verify(repository).deleteById(incorrectCustomerId());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameCustomerServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultCustomer()));
            Mockito.when(repository.save(any()))
                    .thenReturn(defaultCustomerWithName("newName"));

            customerService = new CustomerService(repository, basketRepository);
            customerService.rename(defaultCustomerId(), "newName");

            Mockito.verify(repository).findById(defaultCustomerId());
            Mockito.verify(repository).save(defaultCustomerWithName("newName"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(() -> customerService.rename(defaultCustomerId(), "new . Name"))
                    .isInstanceOf(CustomerIllegalNameException.class);

            Mockito.verify(repository).findById(defaultCustomerId());
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            Mockito.when(repository.findById(nonExistingCustomerId()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(() -> customerService.rename(nonExistingCustomerId(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(nonExistingCustomerId());
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            Mockito.when(repository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository, basketRepository);
            assertThatThrownBy(() -> customerService.rename(incorrectCustomerId(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);

            Mockito.verify(repository).findById(incorrectCustomerId());
        }
    }
}