package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.exception.SqlCommandException;
import org.lager.exception.SqlConnectionException;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SQL Connector")
class SqlConnectorTest implements WithAssertions {

    private SqlConnector connector;
    @Mock
    private ConnectionSupplier connectionSupplier;
    @Mock
    private Connection mockConnection;

    @Nested
    @DisplayName("receives from DB")
    class receiveFromDB {
        @Mock
        private SqlFunction mockCommand;
        @Mock
        private SqlDecoder<Optional<String>> mockDecoder;
        @Mock
        private ResultSet mockResultSet;

        @Test
        @DisplayName("and returns a result")
        void properCase() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.when(mockCommand.execute(mockConnection)).thenReturn(mockResultSet);
            Mockito.when(mockDecoder.decode(mockResultSet)).thenReturn(Optional.of("result"));

            //When
            connector = new SqlConnector(connectionSupplier);
            Optional<String> result = connector.receiveFromDB(mockCommand, mockDecoder);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand).execute(mockConnection);
            Mockito.verify(mockConnection).commit();
            Mockito.verify(mockDecoder).decode(mockResultSet);
            Mockito.verify(mockResultSet).close();
            Mockito.verify(mockConnection).close();
            assertThat(result).isEqualTo(Optional.of("result"));
        }

        @Test
        @DisplayName("and returns an empty Optional")
        void emptyCase() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.when(mockCommand.execute(mockConnection)).thenReturn(mockResultSet);
            Mockito.when(mockDecoder.decode(mockResultSet)).thenReturn(Optional.empty());

            //When
            connector = new SqlConnector(connectionSupplier);
            Optional<String> result = connector.receiveFromDB(mockCommand, mockDecoder);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand).execute(mockConnection);
            Mockito.verify(mockConnection).commit();
            Mockito.verify(mockDecoder).decode(mockResultSet);
            Mockito.verify(mockResultSet).close();
            Mockito.verify(mockConnection).close();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("and throws an exception during Query execution")
        void throwsExceptionDuringQueryExecution() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doThrow(SQLException.class).when(mockCommand).execute(mockConnection);

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.receiveFromDB(mockCommand, mockDecoder))
                    .isInstanceOf(SqlCommandException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand).execute(mockConnection);
            Mockito.verify(mockConnection).rollback();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and throws an exception during Query execution and cannot rollback")
        void throwsExceptionDuringRollbackExecution() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doThrow(SQLException.class).when(mockCommand).execute(mockConnection);
            Mockito.doThrow(SQLException.class).when(mockConnection).rollback();

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.receiveFromDB(mockCommand, mockDecoder))
                    .isInstanceOf(SqlCommandException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand).execute(mockConnection);
            Mockito.verify(mockConnection).rollback();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and throws an exception during Connection's establishing")
        void throwsExceptionDuringConnectionEstablishing () throws SQLException {
            //Given
            Mockito.doThrow(SQLException.class).when(connectionSupplier).get();

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.receiveFromDB(mockCommand, mockDecoder))
                    .isInstanceOf(SqlConnectionException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
        }
    }

    @Nested
    @DisplayName("sends to DB")
    class sendToDB {
        @Mock
        private SqlProcedure mockCommand1;
        @Mock
        private SqlProcedure mockCommand2;

        @Test
        @DisplayName("and executes properly")
        void properCase() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doNothing().when(mockCommand1).execute(mockConnection);
            Mockito.doNothing().when(mockCommand2).execute(mockConnection);
            Mockito.doNothing().when(mockConnection).commit();

            //When
            connector = new SqlConnector(connectionSupplier);
            connector.sendToDB(mockCommand1, mockCommand2);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand1).execute(mockConnection);
            Mockito.verify(mockCommand2).execute(mockConnection);
            Mockito.verify(mockConnection).commit();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and executes properly with Array")
        void properCaseWithArray() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doNothing().when(mockCommand1).execute(mockConnection);
            Mockito.doNothing().when(mockCommand2).execute(mockConnection);
            Mockito.doNothing().when(mockConnection).commit();

            //When
            connector = new SqlConnector(connectionSupplier);
            connector.sendToDB(mockCommand1, new SqlProcedure[]{mockCommand2});

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand1).execute(mockConnection);
            Mockito.verify(mockCommand2).execute(mockConnection);
            Mockito.verify(mockConnection).commit();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and throws an exception")
        void command2throwsException() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doNothing().when(mockCommand1).execute(mockConnection);
            Mockito.doThrow(SQLException.class).when(mockCommand2).execute(mockConnection);

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.sendToDB(mockCommand1, mockCommand2))
                    .isInstanceOf(SqlCommandException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand1).execute(mockConnection);
            Mockito.verify(mockCommand2).execute(mockConnection);
            Mockito.verify(mockConnection).rollback();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and throws an exception")
        void command1throwsException() throws SQLException {
            //Given
            Mockito.when(connectionSupplier.get()).thenReturn(mockConnection);
            Mockito.doThrow(SQLException.class).when(mockCommand1).execute(mockConnection);

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.sendToDB(mockCommand1, mockCommand2))
                    .isInstanceOf(SqlCommandException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
            Mockito.verify(mockCommand1).execute(mockConnection);
            Mockito.verify(mockConnection).rollback();
            Mockito.verify(mockConnection).close();
        }

        @Test
        @DisplayName("and throws an exception during Connection's establishing")
        void throwsExceptionDuringConnectionEstablishing () throws SQLException {
            //Given
            Mockito.doThrow(SQLException.class).when(connectionSupplier).get();

            //When
            connector = new SqlConnector(connectionSupplier);
            assertThatThrownBy(() -> connector.sendToDB(mockCommand1, mockCommand2))
                    .isInstanceOf(SqlConnectionException.class);

            //Then
            Mockito.verify(connectionSupplier).get();
        }
    }
}