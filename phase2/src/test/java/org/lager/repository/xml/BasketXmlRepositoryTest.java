package org.lager.repository.xml;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.RepositoryException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.lager.BasketFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basket XML Repository")
class BasketXmlRepositoryTest implements WithAssertions {

    @Mock
    private  XmlEditor xmlEditor;

    private final BasketXmlMapper xmlMapper = new BasketXmlMapper();

    private BasketXmlRepository repository;

    @Nested
    @DisplayName("saves")
    class SaveBasketRepository {

        @Test
        @DisplayName("but XML File cannot be read/write")
        void nonExisitngFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(xmlEditor).loadFromFile();
            Mockito.doThrow(IOException.class)
                    .when(xmlEditor).saveToFile(Mockito.any());


            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThatThrownBy(() -> repository.save(defaultBasket()))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(xmlEditor).loadFromFile();
        }

        @Test
        @DisplayName("NULL basket")
        void nullBasket() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(xmlEditor).loadFromFile();

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThatThrownBy(() -> repository.save(null))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(xmlEditor).loadFromFile();
        }

        @Test
        @DisplayName("first Record in non exisitng XML")
        void firstInNonExistingFile() throws IOException {
            Mockito.doThrow(IOException.class)
                    .when(xmlEditor).loadFromFile();
            Mockito.doNothing()
                    .when(xmlEditor).saveToFile(Mockito.any());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            repository.save(defaultBasket());

            Mockito.verify(xmlEditor).loadFromFile();
            Mockito.verify(xmlEditor).saveToFile(defaultXmlList());
        }

        @Test
        @DisplayName("first Record in empty File")
        void firstInEmpty() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(new XmlBasketsList(List.of()));
            Mockito.doNothing()
                    .when(xmlEditor).saveToFile(Mockito.any());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            repository.save(defaultBasket());

            Mockito.verify(xmlEditor).loadFromFile();
            Mockito.verify(xmlEditor).saveToFile(defaultXmlList());
        }

        @Test
        @DisplayName("another Record in XML with one record")
        void anotherOneInExisting() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(defaultXmlList());
            Mockito.doNothing()
                    .when(xmlEditor).saveToFile(Mockito.any());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            repository.save(anotherBasket());

            Mockito.verify(xmlEditor).loadFromFile();
            Mockito.verify(xmlEditor).saveToFile(anotherXmlList());
        }
    }
    @Nested
    @DisplayName("reads")
    class ReadBasketRepository {

        @Test
        @DisplayName("NULL ID")
        void nullId() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(new XmlBasketsList(List.of()));

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThatThrownBy(() -> repository.read(null))
                    .isInstanceOf(RepositoryException.class);
            Mockito.verify(xmlEditor).loadFromFile();
        }

        @Test
        @DisplayName("non-existing ID")
        void nonExisting() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(new XmlBasketsList(List.of()));

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThat(repository.read(defaultCustomerId()))
                    .isEmpty();
            Mockito.verify(xmlEditor).loadFromFile();
        }

        @Test
        @DisplayName("exisitng ID")
        void existing() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(defaultXmlList());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThat(repository.read(defaultCustomerId()))
                    .isEqualTo(Optional.of(defaultBasket()));
        }
    }

    @Nested
    @DisplayName("deletes")
    class DeleteBasketRepository {

        @Test
        @DisplayName("NULL ID")
        void nullId() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(new XmlBasketsList(List.of()));

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            assertThatThrownBy(() -> repository.delete(null))
                    .isInstanceOf(RepositoryException.class);

            Mockito.verify(xmlEditor).loadFromFile();
        }

        @Test
        @DisplayName("non-exisitng ID")
        void nonExisting() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(defaultXmlList());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            repository.delete(anotherCustomerId());

            Mockito.verify(xmlEditor).loadFromFile();
            Mockito.verify(xmlEditor).saveToFile(defaultXmlList());
        }

        @Test
        @DisplayName("exisitng ID")
        void existing() throws IOException {
            Mockito.when(xmlEditor.loadFromFile())
                    .thenReturn(anotherXmlList());

            repository = new BasketXmlRepository(xmlEditor, xmlMapper);
            repository.delete(anotherCustomerId());

            Mockito.verify(xmlEditor).loadFromFile();
            Mockito.verify(xmlEditor).saveToFile(defaultXmlList());
        }
    }
}