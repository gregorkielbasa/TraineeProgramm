package org.lager.repository.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Basket;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lager.BasketFixtures.defaultBasket;
import static org.lager.BasketFixtures.defaultCustomerId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basket SQL Repository")
class BasketSqlRepositoryTest {

    BasketSqlRepository repository;
    @Mock
    BasketSqlMapper mockMapper;
    @Mock
    SqlConnector mockConnector;
    @Mock
    SqlProcedure initCommand;

    @BeforeEach
    void init() {
        Mockito.when(mockMapper.getInitialCommand()).thenReturn(initCommand);

        repository = new BasketSqlRepository(mockMapper, mockConnector);

        Mockito.verify(mockConnector).sendToDB(initCommand);
    }

    @Nested
    @DisplayName("executes Read")
    class ReadCommand {

        @Mock
        SqlFunction mockCommand;
        @Mock
        SqlDecoder<Optional<Basket>> mockDecoder;

        @Test
        @DisplayName("and gets a Basket")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultBasket()));

            //When
            Optional<Basket> result = repository.read(defaultCustomerId());

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(Optional.of(defaultBasket()));
        }

        @Test
        @DisplayName("and gets an empty Optional")
        void emptyCse() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            Optional<Basket> result = repository.read(defaultCustomerId());

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).receiveFromDB(any(), any());

            //When
            assertThatThrownBy(() -> repository.read(defaultCustomerId()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
        }
    }

    @Nested
    @DisplayName("executes Delete")
    class DeleteCommand {
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("and works")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getDeleteWholeBasketCommand(anyLong())).thenReturn(mockCommand);

            //When
            repository.delete(defaultCustomerId());

            //Then
            Mockito.verify(mockMapper).getDeleteWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockConnector).sendToDB(mockCommand);
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getDeleteWholeBasketCommand(anyLong())).thenReturn(mockCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(any());

            //When
            assertThatThrownBy(() -> repository.delete(defaultCustomerId()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getDeleteWholeBasketCommand(defaultCustomerId());
        }
    }

    @Nested
    @DisplayName("executes Save")
    class SaveCommand {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Basket>> mockDecoder;
        @Mock
        SqlProcedure mockDeleteCommand;

        SqlProcedure[] mockInsertCommands = new SqlProcedure[2];

        @Test
        @DisplayName("but Basket is present")
        void existingBasket() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultBasket()));
            Mockito.when(mockMapper.getDeleteWholeBasketCommand(anyLong())).thenReturn(mockDeleteCommand);
            Mockito.when(mockMapper.getInsertWholeBasketCommands(any())).thenReturn(mockInsertCommands);

            //When
            repository.save(defaultBasket());

            //Then

            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockMapper).getDeleteWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getInsertWholeBasketCommands(defaultBasket());
            Mockito.verify(mockConnector).sendToDB(mockDeleteCommand, mockInsertCommands);
        }

        @Test
        @DisplayName("but Basket is present and throws an Exception")
        void existingAndThrowsException() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultBasket()));
            Mockito.when(mockMapper.getDeleteWholeBasketCommand(anyLong())).thenReturn(mockDeleteCommand);
            Mockito.when(mockMapper.getInsertWholeBasketCommands(any())).thenReturn(mockInsertCommands);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(mockDeleteCommand, mockInsertCommands);

            //When
            assertThatThrownBy(() -> repository.save(defaultBasket()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertWholeBasketCommands(defaultBasket());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
        }

        @Test
        @DisplayName("and inserts a new Basket")
        void newBasket() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());
            Mockito.when(mockMapper.getInsertWholeBasketCommands(any())).thenReturn(mockInsertCommands);

            //When
            repository.save(defaultBasket());

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertWholeBasketCommands(defaultBasket());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector).sendToDB(mockInsertCommands);
        }

        @Test
        @DisplayName("and inserts a new Basket and throws an Exception")
        void newBasketAndThrowsException() {
            //Given
            Mockito.when(mockMapper.getReadWholeBasketCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            Mockito.when(mockMapper.getInsertWholeBasketCommands(any())).thenReturn(mockInsertCommands);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(mockInsertCommands);

            //When
            assertThatThrownBy(() -> repository.save(defaultBasket()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadWholeBasketCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertWholeBasketCommands(defaultBasket());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
        }
    }

    @Nested
    @DisplayName("throws an Exception")
    class ThrowsException {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Basket>> mockDecoder;
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("when fails to initialize tables")
        void initialisationFail() {
            Mockito.when(mockMapper.getInitialCommand()).thenReturn(initCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(initCommand);

            assertThatThrownBy(() -> new BasketSqlRepository(mockMapper, mockConnector))
                    .isInstanceOf(RepositoryException.class);
        }

        @Test
        @DisplayName("when tries to save NULL Basket")
        void nullBasket() {
            assertThatThrownBy(() -> repository.save(null))
                    .isInstanceOf(RepositoryException.class);
        }

        @Test
        @DisplayName("when tries to read NULL ID")
        void nullId() {
            assertThatThrownBy(() -> repository.read(null))
                    .isInstanceOf(RepositoryException.class);
        }
    }
}

