package org.lager.repository.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Order;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lager.OrderFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order SQL Repository")
class OrderSqlRepositoryTest {

    OrderSqlRepository repository;
    @Mock
    OrderSqlMapper mockMapper;
    @Mock
    SqlConnector mockConnector;
    @Mock
    SqlProcedure initCommand1;
    @Mock
    SqlProcedure initCommand2;

    @BeforeEach
    void init() {
        Mockito.when(mockMapper.getInitialOrderCommand()).thenReturn(initCommand1);
        Mockito.when(mockMapper.getInitialOrderItemCommand()).thenReturn(initCommand2);

        repository = new OrderSqlRepository(mockMapper, mockConnector);

        Mockito.verify(mockConnector).sendToDB(initCommand1, initCommand2);
    }

    @Nested
    @DisplayName("NextAvailableId")
    class NextAvailableIdTest {

        @Mock
        SqlFunction mockCommand;
        @Mock
        SqlDecoder<Optional<Order>> mockDecoder;

        @Test
        @DisplayName("and it works")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getOrderWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultOrder()));

            //When
            long result = repository.getNextAvailableId();

            //Then
            Mockito.verify(mockMapper).getOrderWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(defaultId() + 1);
        }

        @Test
        @DisplayName("and it gives default ID")
        void emptyDB() {
            //Given
            Mockito.when(mockMapper.getOrderWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            long result = repository.getNextAvailableId();

            //Then
            Mockito.verify(mockMapper).getOrderWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(defaultId());
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getOrderWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).receiveFromDB(any(), any());

            //When
            assertThatThrownBy(() -> repository.getNextAvailableId())
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getOrderWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
        }
    }
    
    @Nested
    @DisplayName("executes Read")
    class ReadCommand {

        @Mock
        SqlFunction mockCommand;
        @Mock
        SqlDecoder<Optional<Order>> mockDecoder;

        @Test
        @DisplayName("and gets an Order")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultOrder()));

            //When
            Optional<Order> result = repository.read(defaultCustomerId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(Optional.of(defaultOrder()));
        }

        @Test
        @DisplayName("and gets an empty Optional")
        void emptyCse() {
            //Given
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            Optional<Order> result = repository.read(defaultCustomerId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).receiveFromDB(any(), any());

            //When
            assertThatThrownBy(() -> repository.read(defaultCustomerId()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultCustomerId());
            Mockito.verify(mockMapper).getResultSetDecoder();
        }
    }
    
    @Nested
    @DisplayName("executes Delete")
    class DeleteCommand {

        @Test
        @DisplayName("and gets an Order")
        void properCase() {
            assertThatThrownBy(() -> repository.delete(defaultCustomerId()))
                    .isInstanceOf(RepositoryException.class);
        }
    }

    @Nested
    @DisplayName("executes Save")
    class SaveCommand {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Order>> mockDecoder;

        SqlProcedure[] mockInsertCommands = new SqlProcedure[2];

        @Test
        @DisplayName("but Order is present")
        void existingCustomer() {
            //Given
            Mockito.when(mockMapper.getInsertOrderCommands(any())).thenReturn(mockInsertCommands);
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultOrder()));

            //When
            assertThatThrownBy(() -> repository.save(defaultOrder()))
                    .isInstanceOf(RepositoryException.class);

            //Then

            Mockito.verify(mockMapper).getInsertOrderCommands(defaultOrder());
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
        }

        @Test
        @DisplayName("and inserts a new Order")
        void newBasket() {
            //Given
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());
            Mockito.when(mockMapper.getInsertOrderCommands(any())).thenReturn(mockInsertCommands);

            //When
            repository.save(defaultOrder());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertOrderCommands(defaultOrder());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector).sendToDB(mockInsertCommands);
        }

        @Test
        @DisplayName("and inserts a new Basket and throws an Exception")
        void newBasketAndThrowsException() {
            //Given
            Mockito.when(mockMapper.getReadCommand(anyLong())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            Mockito.when(mockMapper.getInsertOrderCommands(any())).thenReturn(mockInsertCommands);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(mockInsertCommands);

            //When
            assertThatThrownBy(() -> repository.save(defaultOrder()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertOrderCommands(defaultOrder());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
        }
    }

    @Nested
    @DisplayName("throws an Exception")
    class ThrowsException {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Order>> mockDecoder;
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("when fails to initialize tables")
        void initialisationFail() {
            Mockito.when(mockMapper.getInitialOrderCommand()).thenReturn(initCommand1);
            Mockito.when(mockMapper.getInitialOrderItemCommand()).thenReturn(initCommand2);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(initCommand1, initCommand2);

            assertThatThrownBy(() -> new OrderSqlRepository(mockMapper, mockConnector))
                    .isInstanceOf(RepositoryException.class);
        }

        @Test
        @DisplayName("when tries to save NULL Order")
        void nullProduct() {
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