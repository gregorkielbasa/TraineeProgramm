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
            Mockito.when(repository.getNextAvailableId())
                    .thenReturn(defaultId());
            Mockito.doNothing().when(repository).save(Mockito.any());

            customerService = new CustomerService(repository);
            Customer newCustomer = customerService.create(defaultName());

            assertThat(newCustomer).isEqualTo(defaultCustomer());
            Mockito.verify(repository).save(defaultCustomer());
        }


        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() {
            Mockito.when(repository.getNextAvailableId())
                    .thenReturn(defaultId());

            customerService = new CustomerService(repository);

            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() {
            Mockito.when(repository.getNextAvailableId())
                    .thenReturn(defaultId());

            customerService = new CustomerService(repository);

            assertThatThrownBy(() -> customerService.create(incorrectName()))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer but repository cannot save")
        void repositoryException() {
            Mockito.when(repository.getNextAvailableId())
                    .thenReturn(defaultId());
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
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository);
            assertThat(customerService.search(defaultId())).isEqualTo(
                    Optional.of(defaultCustomer())
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThat(customerService.search(defaultId())).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectId()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThat(customerService.search(incorrectId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository);
            customerService.validatePresence(defaultId());

            Mockito.verify(repository).read(defaultId());
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            Mockito.when(repository.read(defaultId()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.validatePresence(defaultId()))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.when(repository.read(incorrectId()))
                    .thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.validatePresence(incorrectId()))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }

    @Nested
    @DisplayName("when deletes")
    class RemoveCustomerServiceTest {

        @Test
        @DisplayName("existing one")
        void existingID() {
            Mockito.doNothing().when(repository).delete(defaultId());

            customerService = new CustomerService(repository);
            customerService.delete(defaultId());

            Mockito.verify(repository).delete(defaultId());
        }


        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            Mockito.doNothing().when(repository).delete(incorrectId());

            customerService = new CustomerService(repository);
            customerService.delete(incorrectId());

            Mockito.verify(repository).delete(incorrectId());
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameCustomerServiceTest {

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() {
            Mockito.when(repository.read(defaultId())).thenReturn(Optional.of(defaultCustomer()));
            Mockito.doNothing().when(repository).save(Mockito.any());

            customerService = new CustomerService(repository);
            customerService.rename(defaultId(), "newName");

            Mockito.verify(repository).save(customerWithName("newName"));
        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            Mockito.when(repository.read(defaultId())).thenReturn(Optional.of(defaultCustomer()));

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(defaultId(), "new . Name"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            Mockito.when(repository.read(nonExistingId())).thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(nonExistingId(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            Mockito.when(repository.read(incorrectId())).thenReturn(Optional.empty());

            customerService = new CustomerService(repository);
            assertThatThrownBy(() -> customerService.rename(incorrectId(), "newName"))
                    .isInstanceOf(NoSuchCustomerException.class);
        }
    }
}