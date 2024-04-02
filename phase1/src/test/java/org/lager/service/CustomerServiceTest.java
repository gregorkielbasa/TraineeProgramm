package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("customerService")
class CustomerServiceTest implements WithAssertions {

    @Mock
    private CustomerRepository repository;
    @Captor
    private ArgumentCaptor<Customer> argumentCaptor;

    CustomerService customerService = new CustomerService(repository);

    @Nested
    @DisplayName("tests create() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one Customer")
        void properOne() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService.create(defaultName());

            Mockito.verify(repository).getNextAvailableNumber();
            Mockito.verify(repository).create(defaultNumber(), argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).isEqualTo(defaultCustomer());

        }

        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            assertThatThrownBy(() -> customerService.create(incorrectName()))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer but repository cannot save")
        void repositoryException() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.doThrow(new RepositoryException(Mockito.any()))
                    .when(repository).create(defaultNumber(), argumentCaptor.capture());

            assertThatThrownBy(() -> customerService.create(defaultName()))
                    .isInstanceOf(RepositoryException.class);
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultCustomer()));

            assertThat(customerService.search(defaultNumber())).isEqualTo(
                    Optional.of(defaultCustomer())
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            assertThat(customerService.search(defaultNumber())).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            assertThat(customerService.search(incorrectNumber())).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService.validatePresence(defaultNumber());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.validatePresence(defaultNumber()))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.validatePresence(incorrectNumber()))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }

    @Nested
    @DisplayName("when removes")
    class RemoveCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.doNothing().when(repository).delete(defaultNumber());

            customerService.remove(defaultNumber());

            Mockito.verify(repository).delete(defaultNumber());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).delete(defaultNumber());

            customerService.remove(incorrectNumber());

            Mockito.verify(repository).delete(defaultNumber());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameCustomerServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.doNothing().when(repository).update(defaultNumber(), defaultCustomer());

            customerService.rename(defaultNumber(), "newName");

            Mockito.verify(repository).update(defaultNumber(), Mockito.any());
            assertThat(argumentCaptor.getValue()).isEqualTo(customerWithName("newName"));
        }
//
//        @Test
//        @DisplayName("existing one with a new invalid name throws an exception")
//        void invalidNameExistingID() {
//            assertThatThrownBy(() -> customerService.rename(CUSTOMER_NUMBER_1, "new . Name"))
//                    .isInstanceOf(CustomerIllegalNameException.class);
//        }
//
//        @Test
//        @DisplayName("non-existing one throws an exception")
//        void nonExistingID() {
//            assertThatThrownBy(() -> customerService.rename(999_999_999, "newName"))
//                    .isInstanceOf(NoSuchCustomerException.class);
//        }
//
//        @Test
//        @DisplayName("invalid ID throws an exception")
//        void invalidID() {
//            assertThatThrownBy(() -> customerService.rename(1, "newName"))
//                    .isInstanceOf(NoSuchCustomerException.class);
//        }
    }
}