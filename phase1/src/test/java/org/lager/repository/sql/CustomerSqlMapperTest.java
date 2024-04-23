package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;

import static org.lager.CustomerFixtures.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer SQL Mapper")
class CustomerSqlMapperTest implements WithAssertions {

    CustomerSqlMapper mapper = new CustomerSqlMapper();

    @Nested
    @DisplayName("decodes ResultSet")
    class getResultSetDecoder {
        @Mock
        ResultSet mockResultSet;

        @Test
        @DisplayName("and works")
        void properCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getString("name")).thenReturn(defaultName());

            //When
            ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();
            Optional<Customer> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.of(defaultCustomer()));
        }

        @Test
        @DisplayName("but answer is empty")
        void emptyCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(false);

            //When
            ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();
            Optional<Customer> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Customer's name is NULL")
        void nullName() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getString("name")).thenReturn(null);

            //When
            ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();
            Optional<Customer> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Customer's ID is NULL")
        void nullID() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(0L);
            Mockito.when(mockResultSet.getString("name")).thenReturn(defaultName());

            //When
            ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();
            Optional<Customer> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Customer's name is NULL")
        void invalidNameLabel() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getString("name")).thenThrow(SQLException.class);

            //When
            ResultSetDecoder<Optional<Customer>> decoder = mapper.getResultSetDecoder();
            Optional<Customer> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.empty());
        }


    }

    @Nested
    @DisplayName("executes command")
    class Command {

        @Mock
        Connection mockConnection;

        @Nested
        @DisplayName("InitialCommand")
        class InitialCommand {

            @Mock
            Statement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);

                //When
                CommandUpdate command = mapper.getInitialCommand();
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).close();
                Mockito.verify(mockStatement).execute("""
                        CREATE TABLE IF NOT EXISTS customers (
                        id bigint PRIMARY KEY,
                        name character varying(24) NOT NULL
                        );""");
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
                Mockito.doThrow(SQLException.class).when(mockStatement).execute(any());

                //When
                CommandUpdate command = mapper.getInitialCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).close();
                Mockito.verify(mockStatement).execute(any());
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.doThrow(SQLException.class).when(mockConnection).createStatement();

                //When
                CommandUpdate command = mapper.getInitialCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).createStatement();
            }
        }

        @Nested
        @DisplayName("CustomerWithHighestIdCommand")
        class CustomerWithHighestIdCommand {

            @Mock
            PreparedStatement mockStatement;
            @Mock
            ResultSet mockResultSet;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);

                //When
                CommandQuery command = mapper.getCustomerWithHighestIdCommand();
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;");
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
                assertThat(result).isEqualTo(mockResultSet);
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery()).thenThrow(SQLException.class);

                //When
                CommandQuery command = mapper.getCustomerWithHighestIdCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;");
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                CommandQuery command = mapper.getCustomerWithHighestIdCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;");
            }
        }

        @Nested
        @DisplayName("ReadCommand")
        class ReadCommand {

            @Mock
            PreparedStatement mockStatement;
            @Mock
            ResultSet mockResultSet;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);

                //When
                CommandQuery command = mapper.getReadCommand(12345L);
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM Customers WHERE id=?;");
                Mockito.verify(mockStatement).setLong(1, 12345L);
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
                assertThat(result).isEqualTo(mockResultSet);
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery()).thenThrow(SQLException.class);

                //When
                CommandQuery command = mapper.getReadCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM Customers WHERE id=?;");
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                CommandQuery command = mapper.getReadCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM Customers WHERE id=?;");
            }
        }

        @Nested
        @DisplayName("DeleteCommand")
        class DeleteCommand {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                CommandUpdate command = mapper.getDeleteCommand(12345L);
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM customers WHERE id=?;");
                Mockito.verify(mockStatement).setLong(1, 12345L);
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getDeleteCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM customers WHERE id=?;");
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getDeleteCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM customers WHERE id=?;");
            }
        }

        @Nested
        @DisplayName("InsertCommand")
        class InsertCommand {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                CommandUpdate command = mapper.getInsertCommand(defaultCustomer());
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO Customers VALUES (?, ?);");
                Mockito.verify(mockStatement).setLong(1, defaultId());
                Mockito.verify(mockStatement).setString(2, defaultName());
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getInsertCommand(defaultCustomer());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO Customers VALUES (?, ?);");
                Mockito.verify(mockStatement).setLong(1, defaultId());
                Mockito.verify(mockStatement).setString(2, defaultName());
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getInsertCommand(defaultCustomer());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO Customers VALUES (?, ?);");
            }
        }

        @Nested
        @DisplayName("UpdateNameCommand")
        class UpdateNameCommand {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                CommandUpdate command = mapper.getUpdateNameCommand(defaultCustomer());
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE Customers SET name=? WHERE id=?;");
                Mockito.verify(mockStatement).setString(1, defaultName());
                Mockito.verify(mockStatement).setLong(2, defaultId());
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getUpdateNameCommand(defaultCustomer());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE Customers SET name=? WHERE id=?;");
                Mockito.verify(mockStatement).setString(1, defaultName());
                Mockito.verify(mockStatement).setLong(2, defaultId());
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                CommandUpdate command = mapper.getUpdateNameCommand(defaultCustomer());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE Customers SET name=? WHERE id=?;");
            }
        }
    }
}