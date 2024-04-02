package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.lager.CustomerFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer CSV Repository")
class CustomerCsvRepositoryTest implements WithAssertions {


    @Captor
    private ArgumentCaptor<List<String>> argumentCaptor;
    @Mock
    private CsvEditor csvEditor;

    @Nested
    @DisplayName("when loads and")
    class CustomerCsvRepositoryInstanced {

        @Test
        @DisplayName("CSV File doesn't exist")
        void nonExistingFile() throws IOException {
            Mockito.doThrow(IOException.class).when(csvEditor).loadFromFile();

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor);
            customerCsvRepository.create(defaultNumber(), defaultCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsOnly(defaultCustomerAsCsvRecord());
        }

        @Test
        @DisplayName("CSV File is empty")
        void emptyFile() throws IOException {
            Mockito.when(csvEditor.loadFromFile()).thenReturn(List.of());

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor);
            customerCsvRepository.create(defaultNumber(), defaultCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsOnly(defaultCustomerAsCsvRecord());
        }

        @Test
        @DisplayName("CSV File has one record")
        void oneRecord() throws IOException {
            Mockito.when(csvEditor.loadFromFile()).thenReturn(List.of(defaultCustomerAsCsvRecord()));

            CustomerCsvRepository customerCsvRepository = new CustomerCsvRepository(csvEditor);
            customerCsvRepository.create(anotherNumber(), anotherCustomer());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultCustomerAsCsvRecord(), anotherCustomerAsCsvRecord());
        }
    }
}