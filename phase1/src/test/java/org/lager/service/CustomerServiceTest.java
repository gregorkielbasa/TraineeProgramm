package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service")
class CustomerServiceTest implements WithAssertions {

    @Mock
    private CustomerRepository repository;

    CustomerService customerService;

    @Nested
    @DisplayName("tests create() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one Customer")
        void properOne() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.doNothing().when(repository).save(Mockito.any());

            customerService = new CustomerService(repository);
            Customer newCustomer = customerService.create(defaultName());

            assertThat(newCustomer).isEqualTo(defaultCustomer());
            Mockito.verify(repository).save(defaultCustomer());
        }


        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            customerService = new CustomerService(repository);

            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());

            customerService = new CustomerService(repository);

            assertThatThrownBy(() -> customerService.create(incorrectName()))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer but repository cannot save")
        void repositoryException() {
            Mockito.when(repository.getNextAvailableNumber())
                    .thenReturn(defaultNumber());
            Mockito.doThrow(new RepositoryException("any"))
                    .when(repository).save(Mockito.any());

            customerService = new CustomerService(repository);

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

            customerService = new CustomerService(repository);
            assertThat(customerService.search(defaultNumber())).isEqualTo(
                    Optional.of(defaultCustomer())
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThat(customerService.search(defaultNumber())).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThat(customerService.search(incorrectNumber())).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository);
            customerService.validatePresence(defaultNumber());

            Mockito.verify(repository).read(defaultNumber());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultNumber()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.validatePresence(defaultNumber()))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
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

            customerService = new CustomerService(repository);
            customerService.remove(defaultNumber());

            Mockito.verify(repository).delete(defaultNumber());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).delete(incorrectNumber());

            customerService = new CustomerService(repository);
            customerService.remove(incorrectNumber());

            Mockito.verify(repository).delete(incorrectNumber());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameCustomerServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.when(repository.read(defaultNumber())).thenReturn(Optional.of(defaultCustomer()));
            Mockito.doNothing().when(repository).save(Mockito.any());

            customerService = new CustomerService(repository);
            customerService.rename(defaultNumber(), "newName");

            Mockito.verify(repository).save(customerWithName("newName"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            Mockito.when(repository.read(defaultNumber())).thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(defaultNumber(), "new . Name"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            Mockito.when(repository.read(nonExistingNumber())).thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(nonExistingNumber(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            Mockito.when(repository.read(incorrectNumber())).thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(incorrectNumber(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }
}