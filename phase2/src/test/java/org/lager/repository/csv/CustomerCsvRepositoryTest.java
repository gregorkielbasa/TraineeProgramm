package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.RepositoryException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.lager.CustomerFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer CSV Repository")
class CustomerCsvRepositoryTest implements WithAssertions {

    @Captor
    private ArgumentCaptor<List<String>> argumentCaptor;
    @Mock
    private CsvEditor csvEditor;

    private CustomerCsvMapper csvMapper = new CustomerCsvMapper();

    @Nested
    @DisplayName("saves")
    class CustomerRepositorySave {

        @Test
        @DisplayName("but CSV File cannot be read/write")
        void nonExistingFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).loadFromFile();
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).saveToFile(Mockito.anyList());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> customerCsvRepository.save(defaultCustomer()))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("first Record in non exisitng CSV")
        void firstInNonExistingFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).loadFromFile();
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(Mockito.anyList());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            customerCsvRepository.save(defaultCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(defaultCustomerAsCsvRecord()));
        }

        @Test
        @DisplayName("another Record in CSV with one record")
        void anotherOneInExisting() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(defaultCustomerAsCsvRecord()));
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(argumentCaptor.capture());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            customerCsvRepository.save(anotherCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultCustomerAsCsvRecord(), anotherCustomerAsCsvRecord());
        }

        @Test
        @DisplayName("two Record in empty CSV")
        void twoRecord() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(argumentCaptor.capture());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            customerCsvRepository.save(defaultCustomer());
            customerCsvRepository.save(anotherCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor, Mockito.times(2)).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultCustomerAsCsvRecord(), anotherCustomerAsCsvRecord());
            assertThat(customerCsvRepository.getNextAvailableId()).isEqualTo(defaultId() + 2);
        }

        @Test
        @DisplayName("NULL Customer")
        void nullCustomer() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> customerCsvRepository.save(null))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(csvEditor).loadFromFile();
        }

    }

    @Nested
    @DisplayName("gives next available ID")
    class CustomerRepositoryNextId {

        @Test
        @DisplayName("but File is empty")
        void emptyFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).loadFromFile();

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThat(customerCsvRepository.getNextAvailableId())
                    .isEqualTo(defaultId());
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("and File has two record")
        void nonEmptyFile() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherCustomerAsCsvRecord(), defaultCustomerAsCsvRecord()));

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThat(customerCsvRepository.getNextAvailableId())
                    .isEqualTo(defaultId() + 2);
            Mockito.verify(csvEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("reads")
    class CustomerRepositoryRead {

        @Test
        @DisplayName("NULL ID")
        void nullId() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> customerCsvRepository.read(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing ID")
        void nonExisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThat(customerCsvRepository.read(incorrectId()))
                    .isEmpty();
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("existing ID")
        void exisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherCustomerAsCsvRecord(), defaultCustomerAsCsvRecord()));

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThat(customerCsvRepository.read(defaultId()))
                    .isEqualTo(Optional.of(defaultCustomer()));
            Mockito.verify(csvEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("deletes")
    class CustomerRepositoryDelete {

        @Test
        @DisplayName("NULL ID")
        void nullId() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> customerCsvRepository.delete(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing ID")
        void nonExisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(defaultCustomerAsCsvRecord()));
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(Mockito.anyList());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            customerCsvRepository.delete(incorrectId());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(defaultCustomerAsCsvRecord()));
        }

        @Test
        @DisplayName("existing ID")
        void exisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherCustomerAsCsvRecord(), defaultCustomerAsCsvRecord()));
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(Mockito.anyList());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor, csvMapper);
            customerCsvRepository.delete(defaultId());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(anotherCustomerAsCsvRecord()));
        }
    }
}