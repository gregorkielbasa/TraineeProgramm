package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.CustomerFixtures;
import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.lager.CustomerFixtures.defaultCustomer;
import static org.lager.CustomerFixtures.defaultId;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer SQL Repository")
class CustomerSqlRepositoryTest implements WithAssertions {

    CustomerSqlRepository repository;
    @Mock
    CustomerSqlMapper mockMapper;
    @Mock
    SqlConnector mockConnector;
    @Mock
    SqlProcedure initCommand;

    @BeforeEach
    void init() {
        Mockito.when(mockMapper.getInitialCommand()).thenReturn(initCommand);

        repository = new CustomerSqlRepository(mockMapper, mockConnector);

        Mockito.verify(mockConnector).sendToDB(initCommand);
    }

    @Nested
    @DisplayName("executes getNextAvailableId")
    class getNextAvailableId {

        @Mock
        SqlFunction mockCommand;
        @Mock
        SqlDecoder<Optional<Customer>> mockDecoder;

        @Test
        @DisplayName("and it works")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getCustomerWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultCustomer()));

            //When
            long result = repository.getNextAvailableId();

            //Then
            Mockito.verify(mockMapper).getCustomerWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(defaultId() + 1);
        }

        @Test
        @DisplayName("and it gives default ID")
        void emptyDB() {
            //Given
            Mockito.when(mockMapper.getCustomerWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            long result = repository.getNextAvailableId();

            //Then
            Mockito.verify(mockMapper).getCustomerWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(defaultId());
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getCustomerWithHighestIdCommand()).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).receiveFromDB(any(), any());

            //When
            assertThatThrownBy(() -> repository.getNextAvailableId())
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getCustomerWithHighestIdCommand();
            Mockito.verify(mockMapper).getResultSetDecoder();
        }
    }

    @Nested
    @DisplayName("executes Read")
    class read {

        @Mock
        SqlFunction mockCommand;
        @Mock
        SqlDecoder<Optional<Customer>> mockDecoder;

        @Test
        @DisplayName("and gets a Customer")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultCustomer()));

            //When
            Optional<Customer> result = repository.read(defaultId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(Optional.of(defaultCustomer()));
        }

        @Test
        @DisplayName("and gets an empty Optional")
        void emptyCse() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            Optional<Customer> result = repository.read(defaultId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockConnector).receiveFromDB(mockCommand, mockDecoder);
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).receiveFromDB(any(), any());

            //When
            assertThatThrownBy(() -> repository.read(CustomerFixtures.defaultId()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadCommand(CustomerFixtures.defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
        }
    }

    @Nested
    @DisplayName("executes Delete")
    class delete {
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("and works")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getDeleteCommand(any())).thenReturn(mockCommand);

            //When
            repository.delete(defaultId());

            //Then
            Mockito.verify(mockMapper).getDeleteCommand(defaultId());
            Mockito.verify(mockConnector).sendToDB(mockCommand);
        }

        @Test
        @DisplayName("and throws an Exception")
        void throwsException() {
            //Given
            Mockito.when(mockMapper.getDeleteCommand(any())).thenReturn(mockCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(any());

            //When
            assertThatThrownBy(() -> repository.delete(CustomerFixtures.defaultId()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getDeleteCommand(CustomerFixtures.defaultId());
        }
    }

    @Nested
    @DisplayName("executes Save")
    class save {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Customer>> mockDecoder;
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("but Customer is present")
        void existingCustomer() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultCustomer()));
            Mockito.when(mockMapper.getUpdateNameCommand(any())).thenReturn(mockCommand);

            //When
            repository.save(defaultCustomer());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getUpdateNameCommand(defaultCustomer());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector).sendToDB(mockCommand);
        }

        @Test
        @DisplayName("but Customer is present and throws an Exception")
        void existingAndThrowsException() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultCustomer()));
            Mockito.when(mockMapper.getUpdateNameCommand(any())).thenReturn(mockCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(mockCommand);

            //When
            assertThatThrownBy(() -> repository.save(defaultCustomer()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getUpdateNameCommand(defaultCustomer());
        }

        @Test
        @DisplayName("and inserts a new Customer")
        void newCustomer() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());
            Mockito.when(mockMapper.getInsertCommand(any())).thenReturn(mockCommand);

            //When
            repository.save(defaultCustomer());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertCommand(defaultCustomer());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector).sendToDB(mockCommand);
        }

        @Test
        @DisplayName("and inserts a new Customer and throws an Exception")
        void newCustomerAndThrowsException() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());
            Mockito.when(mockMapper.getInsertCommand(any())).thenReturn(mockCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(mockCommand);

            //When
            assertThatThrownBy(() -> repository.save(defaultCustomer()))
                    .isInstanceOf(RepositoryException.class);

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getInsertCommand(defaultCustomer());
        }
    }

    @Nested
    @DisplayName("throws an Exception")
    class ThrowsException {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Customer>> mockDecoder;
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("when fails to initialize tables")
        void initialisationFail() {
            Mockito.when(mockMapper.getInitialCommand()).thenReturn(initCommand);
            Mockito.doThrow(SqlConnectionException.class).when(mockConnector).sendToDB(initCommand);

            assertThatThrownBy(() -> new CustomerSqlRepository(mockMapper, mockConnector))
                    .isInstanceOf(RepositoryException.class);
        }

        @Test
        @DisplayName("when tries to save NULL Customer")
        void nullCustomer() {
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