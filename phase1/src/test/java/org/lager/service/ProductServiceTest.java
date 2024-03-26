package org.lager.service;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.repository.ProductCsvEditor;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("productService")
class ProductServiceTest implements WithAssertions {

    private final static long PRODUCT_NUMBER_1 = 100_000_000L;
    private final static Product PRODUCT_1 = new Product(PRODUCT_NUMBER_1, "test1");
    private final static long PRODUCT_NUMBER_2 = 100_000_001L;
    private final static Product PRODUCT_2 = new Product(PRODUCT_NUMBER_2, "test2");

    @Captor
    private ArgumentCaptor<List<Product>> argumentCaptor;
    @Mock
    private ProductCsvEditor csvEditor;

    @Nested
    @DisplayName("tests getAll() method and")
    class GetAllTest {

        @Test
        @DisplayName("is empty when no CVS File exists")
        void nonExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenThrow(IOException.class);

            ProductService productService = new ProductService(csvEditor);

            assertThat(productService.getAll()).isEmpty();
        }

        @Test
        @DisplayName("is empty when no CVS File is empty")
        void emptyCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());

            ProductService productService = new ProductService(csvEditor);

            assertThat(productService.getAll()).isEmpty();
        }

        @Test
        @DisplayName("has one record")
        void oneRecordInCSV() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));

            ProductService productService = new ProductService(csvEditor);

            assertThat(productService.getAll())
                    .containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1));
        }

        @Test
        @DisplayName("has more records")
        void moreRecordsInCSV() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1, PRODUCT_2));

            ProductService productService = new ProductService(csvEditor);

            assertThat(productService.getAll())
                    .containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }
    }

    @Nested
    @DisplayName("tests insert() method and")
    class CreateTest {

        @Test
        @DisplayName("adds one to an empty List")
        void nonExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of());
            ProductService productService = new ProductService(csvEditor);

            productService.insert("test1");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1));

        }

        @Test
        @DisplayName("adds 2nd element with new name")
        void oneExistingCsv() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));
            ProductService productService = new ProductService(csvEditor);

            productService.insert("test2");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }

        @Test
        @DisplayName("adds 2nd element with the same name")
        void oneTwoWithSameName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));
            ProductService productService = new ProductService(csvEditor);

            productService.insert("test1");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, new Product(PRODUCT_NUMBER_2, "test1")));
        }

        @Test
        @DisplayName("adds 2nd element to a List with high ProductNumber")
        void highProductNumber() throws IOException {
            Product highProduct = new Product(111_000_000L, "name");
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(highProduct));
            ProductService productService = new ProductService(csvEditor);

            productService.insert("newName");

            Mockito.verify(csvEditor).loadFromFile();
            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(highProduct, new Product(111_000_001L, "newName")));
        }

        @Test
        @DisplayName("a product with null Name should throw an exception")
        void nullName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));
            ProductService productService = new ProductService(csvEditor);

            assertThatThrownBy(() -> productService.insert(null))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product with invalid Name should throw an exception")
        void invalidName() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));
            ProductService productService = new ProductService(csvEditor);

            assertThatThrownBy(() -> productService.insert("Test!!§$%&/()=Test"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("a product but cannot save CSV File")
        void corruptedCsvFile() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1));
            Mockito.doThrow(new IOException())
                    .when(csvEditor).saveToFile(argumentCaptor.capture());
            ProductService productService = new ProductService(csvEditor);

            productService.insert("test2");

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }
    }

    @Nested
    @DisplayName("when searches for")
    class SearchProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1, PRODUCT_2));
            productService = new ProductService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(productService.search(PRODUCT_NUMBER_1)).isEqualTo(
                    Optional.of(PRODUCT_1)
            );
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThat(productService.search(999_999_999)).isEmpty();
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThat(productService.search(1)).isEmpty();
        }
    }

    @Nested
    @DisplayName("when check Presence")
    class ValidatePresenceProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1, PRODUCT_2));
            productService = new ProductService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() {
            assertThat(productService.validatePresence(PRODUCT_NUMBER_1)).isTrue();
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() {
            assertThatThrownBy(() -> productService.validatePresence(999_999_999))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() {
            assertThatThrownBy(() -> productService.validatePresence(1))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }

    @Nested
    @DisplayName("when removes")
    class RemoveProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(PRODUCT_1, PRODUCT_2));
            productService = new ProductService(csvEditor);
        }

        @Test
        @DisplayName("existing one")
        void existingID() throws IOException {
            productService.remove(PRODUCT_NUMBER_1);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_2));
        }

        @Test
        @DisplayName("non-existing one")
        void nonExistingID() throws IOException {
            productService.remove(999_999_999);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }

        @Test
        @DisplayName("invalid ID")
        void invalidID() throws IOException {
            productService.remove(1);

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_1, PRODUCT_2));
        }
    }

    @Nested
    @DisplayName("when renames")
    class RenameProductServiceTest {

        ProductService productService;

        @BeforeEach
        void init() throws IOException {
            Mockito.when(csvEditor.loadFromFile())
                    .thenReturn(List.of(new Product(PRODUCT_NUMBER_1, "oldName"), PRODUCT_2));
            productService = new ProductService(csvEditor);
        }

        @Test
        @DisplayName("existing one with a new proper name")
        void existingID() throws IOException {
            productService.rename(PRODUCT_NUMBER_1, "newName");

            Mockito.verify(csvEditor).saveToFile(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrderElementsOf(List.of(PRODUCT_2, new Product(PRODUCT_NUMBER_1, "newName")));

        }

        @Test
        @DisplayName("existing one with a new invalid name throws an exception")
        void invalidNameExistingID() {
            assertThatThrownBy(() -> productService.rename(PRODUCT_NUMBER_1, "new §$%&/( Name"))
                    .isInstanceOf(ProductIllegalNameException.class);
        }

        @Test
        @DisplayName("non-existing one throws an exception")
        void nonExistingID() {
            assertThatThrownBy(() -> productService.rename(999_999_999, "newName"))
                    .isInstanceOf(NoSuchProductException.class);
        }

        @Test
        @DisplayName("invalid ID throws an exception")
        void invalidID() {
            assertThatThrownBy(() -> productService.rename(1, "newName"))
                    .isInstanceOf(NoSuchProductException.class);
        }
    }
}