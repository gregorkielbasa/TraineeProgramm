package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
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
    }

    @Nested
    @DisplayName("executes Delete")
    class delete {

        @Mock
        SqlFunction mockReadCommand;
        @Mock
        SqlDecoder<Optional<Customer>> mockDecoder;
        @Mock
        SqlProcedure mockCommand;

        @Test
        @DisplayName("and gets a Customer")
        void properCase() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.of(defaultCustomer()));
            Mockito.when(mockMapper.getDeleteCommand(any())).thenReturn(mockCommand);

            //When
            repository.delete(defaultId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getDeleteCommand(defaultId());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector).sendToDB(mockCommand);
        }

        @Test
        @DisplayName("and doesn't get any Customer")
        void emptyCse() {
            //Given
            Mockito.when(mockMapper.getReadCommand(any())).thenReturn(mockReadCommand);
            Mockito.when(mockMapper.getResultSetDecoder()).thenReturn(mockDecoder);
            Mockito.when(mockConnector.receiveFromDB(any(), any())).thenReturn(Optional.empty());

            //When
            repository.delete(defaultId());

            //Then
            Mockito.verify(mockMapper).getReadCommand(defaultId());
            Mockito.verify(mockMapper).getResultSetDecoder();
            Mockito.verify(mockMapper).getDeleteCommand(defaultId());
            Mockito.verify(mockConnector).receiveFromDB(mockReadCommand, mockDecoder);
            Mockito.verify(mockConnector, Mockito.never()).sendToDB(mockCommand);
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