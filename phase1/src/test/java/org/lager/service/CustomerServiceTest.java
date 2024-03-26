package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.repository.CustomerCsvEditor;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("customerService")
class CustomerServiceTest implements WithAssertions {

    private final static long CUSTOMER_NUMBER_1 = 100_000_000L;
    private final static Customer CUSTOMER_1 = new Customer(CUSTOMER_NUMBER_1, "testOne");
    private final static long CUSTOMER_NUMBER_2 = 100_000_001L;
    private final static Customer CUSTOMER_2 = new Customer(CUSTOMER_NUMBER_2, "testTwo");

    @Captor
    private ArgumentCaptor<List<Customer>> argumentCaptor;
    @Mock
    private CustomerCsvEditor csvEditor;

    @Nested
    @DisplayName("tests getAll() method and")
    class GetAllTest {

        @Test
        @DisplayName("is empty when no CVS File exists")
        void nonExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenThrow(IOException.class);

            CustomerService customerService = new CustomerService(csvEditor);

            assertThat(customerService.getAll()).isEmpty();
        }

        @Test
        @DisplayName("is empty when no CVS File is empty")
        void emptyCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            CustomerService customerService = new CustomerService(csvEditor);

            assertThat(customerService.getAll()).isEmpty();
        }

        @Test
        @DisplayName("has one record")
        void oneRecordInCSV() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));

            CustomerService customerService = new CustomerService(csvEditor);

            assertThat(customerService.getAll())
                    .containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1));
        }

        @Test
        @DisplayName("has more records")
        void moreRecordsInCSV() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1, CUSTOMER_2));

            CustomerService customerService = new CustomerService(csvEditor);

            assertThat(customerService.getAll())
                    .containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }
    }

    @Nested
    @DisplayName("tests create() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one to an empty List")
        void nonExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());
            CustomerService customerService = new CustomerService(csvEditor);

            customerService.create("testOne");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1));

        }

        @Test
        @DisplayName("adds 2nd element with new name")
        void oneExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));
            CustomerService customerService = new CustomerService(csvEditor);

            customerService.create("testTwo");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }

        @Test
        @DisplayName("adds 2nd element with the same name")
        void oneTwoWithSameName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));
            CustomerService customerService = new CustomerService(csvEditor);

            customerService.create("testOne");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, new Customer(CUSTOMER_NUMBER_2, "testOne")));
        }

        @Test
        @DisplayName("adds 2nd element to a List with high CustomerNumber")
        void highCustomerNumber() throws IOException {
            Customer highCustomer = new Customer(111_000_000L, "name");
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(highCustomer));
            CustomerService customerService = new CustomerService(csvEditor);

            customerService.create("newName");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(highCustomer, new Customer(111_000_001L, "newName")));
        }

        @Test
        @DisplayName("a customer with null Name should throw an exception")
        void nullName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));
            CustomerService customerService = new CustomerService(csvEditor);

            assertThatThrownBy(() -> customerService.create(null))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer with invalid Name should throw an exception")
        void invalidName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));
            CustomerService customerService = new CustomerService(csvEditor);

            assertThatThrownBy(() -> customerService.create("Test!!§$%&/()=Test"))
                    .isInstanceOf(CustomerIllegalNameException.class);
        }

        @Test
        @DisplayName("a customer but cannot save CSV File")
        void corruptedCsvFile() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1));
            Mockito.doThrow(new IOException())
                    .when(csvEditor).saveToFile(argumentCaptor.capture());
            CustomerService customerService = new CustomerService(csvEditor);

            customerService.create("testTwo");

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1, CUSTOMER_2));
            customerService = new CustomerService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(customerService.search(CUSTOMER_NUMBER_1)).isEqualTo(
                    Optional.of(CUSTOMER_1)
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
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1, CUSTOMER_2));
            customerService = new CustomerService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(customerService.validatePresence(CUSTOMER_NUMBER_1)).isTrue();
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
    @DisplayName("when removes")
    class RemoveCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(CUSTOMER_1, CUSTOMER_2));
            customerService = new CustomerService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() throws IOException {
            customerService.remove(CUSTOMER_NUMBER_1);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_2));
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() throws IOException {
            customerService.remove(999_999_999);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() throws IOException {
            customerService.remove(1);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameCustomerServiceTest {

        CustomerService customerService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(new Customer(CUSTOMER_NUMBER_1, "oldName"), CUSTOMER_2));
            customerService = new CustomerService(csvEditor);
        }

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() throws IOException {
            customerService.rename(CUSTOMER_NUMBER_1, "newName");

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_2, new Customer(CUSTOMER_NUMBER_1, "newName")));

        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            assertThatThrownBy(() -> customerService.rename(CUSTOMER_NUMBER_1, "new . Name"))
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