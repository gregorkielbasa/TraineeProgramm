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

import static org.lager.ProductFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product CSV Repository")
class ProductCsvRepositoryTest implements WithAssertions {


    @Captor
    private ArgumentCaptor<List<String>> argumentCaptor;
    @Mock
    private CsvEditor csvEditor;

    private ProductCsvMapper csvMapper = new ProductCsvMapper();

    @Nested
    @DisplayName("saves")
    class ProductRepositorySave {

        @Test
        @DisplayName("but CSV File cannot be read/write")
        void nonExistingFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).loadFromFile();
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).saveToFile(Mockito.anyList());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> productCsvRepository.save(defaultProduct()))
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

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            productCsvRepository.save(defaultProduct());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(defaultProductAsCsvRecord()));
        }

        @Test
        @DisplayName("another Record in CSV with one record")
        void anotherOneInExisting() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(defaultProductAsCsvRecord()));
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(argumentCaptor.capture());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            productCsvRepository.save(anotherProduct());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultProductAsCsvRecord(), anotherProductAsCsvRecord());
        }

        @Test
        @DisplayName("two Record in empty CSV")
        void twoRecord() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(argumentCaptor.capture());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            productCsvRepository.save(defaultProduct());
            productCsvRepository.save(anotherProduct());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor, Mockito.times(2)).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultProductAsCsvRecord(), anotherProductAsCsvRecord());
            assertThat(productCsvRepository.getNextAvailableNumber()).isEqualTo(defaultNumber() + 2);
        }

        @Test
        @DisplayName("NULL Product")
        void nullProduct() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> productCsvRepository.save(null))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(csvEditor).loadFromFile();
        }

    }

    @Nested
    @DisplayName("gives next available Number")
    class ProductRepositoryNextNumber {

        @Test
        @DisplayName("but File is empty")
        void emptyFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(csvEditor).loadFromFile();

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThat(productCsvRepository.getNextAvailableNumber())
                    .isEqualTo(defaultNumber());
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("and File has two record")
        void nonEmptyFile() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherProductAsCsvRecord(), defaultProductAsCsvRecord()));

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThat(productCsvRepository.getNextAvailableNumber())
                    .isEqualTo(defaultNumber() + 2);
            Mockito.verify(csvEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("reads")
    class ProductRepositoryRead {

        @Test
        @DisplayName("NULL Number")
        void nullNumber() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> productCsvRepository.read(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing Number")
        void nonExisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThat(productCsvRepository.read(incorrectNumber()))
                    .isEmpty();
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("existing Number")
        void exisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherProductAsCsvRecord(), defaultProductAsCsvRecord()));

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThat(productCsvRepository.read(defaultNumber()))
                    .isEqualTo(Optional.of(defaultProduct()));
            Mockito.verify(csvEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("deletes")
    class ProductRepositoryDelete {

        @Test
        @DisplayName("NULL Number")
        void nullNumber() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            assertThatThrownBy(() -> productCsvRepository.delete(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(csvEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing Number")
        void nonExisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(defaultProductAsCsvRecord()));
            Mockito.doNothing()
                    .when(csvEditor).saveToFile(argumentCaptor.capture());

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            productCsvRepository.delete(incorrectNumber());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(defaultProductAsCsvRecord()));
        }

        @Test
        @DisplayName("existing Number")
        void exisitng() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(anotherProductAsCsvRecord(), defaultProductAsCsvRecord()));

            ProductCsvRepository productCsvRepository = new ProductCsvRepository(csvEditor, csvMapper);
            productCsvRepository.delete(defaultNumber());

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(List.of(anotherProductAsCsvRecord()));
        }
    }
}