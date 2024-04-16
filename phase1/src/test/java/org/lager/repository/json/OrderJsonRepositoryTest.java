package org.lager.repository.json;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.lager.OrderFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order JSON Repository")
class OrderJsonRepositoryTest implements WithAssertions {

    @Captor
    private ArgumentCaptor<List<JsonOrder>> argumentCaptor;
    @Mock
    private JsonEditor jsonEditor;

    private OrderJsonMapper jsonMapper = new OrderJsonMapper();

    @Nested
    @DisplayName("saves")
    class OrderRepositorySave {

        @Test
        @DisplayName("but JSON File cannot be read/write")
        void nonExisitngFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(jsonEditor).loadFromFile();
            Mockito.doThrow(IOException.class)
                    .when(jsonEditor).saveToFile(Mockito.anyList());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThatThrownBy(()-> repository.save(defaultOrder()))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(jsonEditor).loadFromFile();
        }

        @Test
        @DisplayName("first Record in non exisitng JSON File")
        void firstInExisitngFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(jsonEditor).loadFromFile();
            Mockito.doNothing()
                    .when(jsonEditor).saveToFile(Mockito.anyList());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            repository.save(defaultOrder());

            Mockito.verify(jsonEditor).loadFromFile();
            Mockito.verify(jsonEditor).saveToFile(List.of(defaultOrderAsJson()));
        }

        @Test
        @DisplayName("another Record in JSON with one record")
        void anotherOneInExisitngFile() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of(defaultOrderAsJson()));
            Mockito.doNothing()
                    .when(jsonEditor).saveToFile(argumentCaptor.capture());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            repository.save(anotherOrder());

            Mockito.verify(jsonEditor).loadFromFile();
            Mockito.verify(jsonEditor).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultOrderAsJson(), anotherOrderAsJson());
        }

        @Test
        @DisplayName("two Records in an empty JSON")
        void twoRecords() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of());
            Mockito.doNothing()
                    .when(jsonEditor).saveToFile(argumentCaptor.capture());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            repository.save(defaultOrder());
            repository.save(anotherOrder());

            Mockito.verify(jsonEditor).loadFromFile();
            Mockito.verify(jsonEditor, Mockito.times(2)).saveToFile(Mockito.anyList());

            assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(defaultOrderAsJson(), anotherOrderAsJson());
            assertThat(repository.getNextAvailableNumber()).isEqualTo(defaultId()+2);
        }

        @Test
        @DisplayName("NULL Order")
        void nullOrder() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThatThrownBy(()-> repository.save(null))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(jsonEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("gives next available id")
    class OrderRepositoryNextId {

        @Test
        @DisplayName("but JSON File is empty")
        void emptyFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(jsonEditor).loadFromFile();

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThat(repository.getNextAvailableNumber())
                    .isEqualTo(defaultId());
            Mockito.verify(jsonEditor).loadFromFile();
        }

        @Test
        @DisplayName("and File has two records")
        void nonEmptyFile() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of(defaultOrderAsJson(), anotherOrderAsJson()));

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThat(repository.getNextAvailableNumber())
                    .isEqualTo(defaultId()+2);
            Mockito.verify(jsonEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("reads")
    class OrderRepositoryRead {

        @Test
        @DisplayName("NULL id")
        void nullId() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThatThrownBy(() -> repository.read(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(jsonEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing id")
        void nonExisitng() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThat(repository.read(defaultId()))
                    .isEmpty();
            Mockito.verify(jsonEditor).loadFromFile();
        }

        @Test
        @DisplayName("existing id")
        void exisitng() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of(defaultOrderAsJson(), anotherOrderAsJson()));

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThat(repository.read(defaultId()))
                    .isEqualTo(Optional.of(defaultOrder()));
            Mockito.verify(jsonEditor).loadFromFile();
        }
    }

    @Nested
    @DisplayName("reads")
    class OrderRepositoryDeletes {

        @Test
        @DisplayName("NULL id")
        void nullId() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            assertThatThrownBy(() -> repository.delete(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(jsonEditor).loadFromFile();
        }

        @Test
        @DisplayName("non existing id")
        void nonExisitng() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of(defaultOrderAsJson()));
            Mockito.doNothing()
                    .when(jsonEditor).saveToFile(Mockito.anyList());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            repository.delete(incorrectId());

            Mockito.verify(jsonEditor).loadFromFile();
            Mockito.verify(jsonEditor).saveToFile(List.of(defaultOrderAsJson()));
        }

        @Test
        @DisplayName("existing id")
        void exisitng() throws IOException {
            Mockito.when(jsonEditor.loadFromFile())
                    .thenReturn(List.of(defaultOrderAsJson(), anotherOrderAsJson()));
            Mockito.doNothing()
                    .when(jsonEditor).saveToFile(Mockito.anyList());

            OrderJsonRepository repository = new OrderJsonRepository(jsonEditor, jsonMapper);
            repository.delete(defaultId());

            Mockito.verify(jsonEditor).loadFromFile();
            Mockito.verify(jsonEditor).saveToFile(List.of(anotherOrderAsJson()));
        }
    }
}