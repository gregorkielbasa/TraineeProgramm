package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.model.Product;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;

import static org.lager.ProductFixtures.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product SQL Mapper")
class ProductSqlMapperTest implements WithAssertions {

    ProductSqlMapper mapper = new ProductSqlMapper();

    @Nested
    @DisplayName("decodes ResultSet")
    class SqlDecoderTest {

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
            SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();
            Optional<Product> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.of(defaultProduct()));
        }

        @Test
        @DisplayName("but answer is empty")
        void emptyCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(false);

            //When
            SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();
            Optional<Product> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Product's name is NULL")
        void nullName() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getString("name")).thenReturn(null);

            //When
            SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();
            Optional<Product> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Product's ID is NULL")
        void nullID() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(0L);
            Mockito.when(mockResultSet.getString("name")).thenReturn(defaultName());

            //When
            SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();
            Optional<Product> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            Mockito.verify(mockResultSet).getLong("id");
            Mockito.verify(mockResultSet).getString("name");
            assertThat(result).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("but Product's name Label is not Present")
        void invalidNameLabel() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getString("name")).thenThrow(SQLException.class);

            //When
            SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();
            Optional<Product> result = decoder.decode(mockResultSet);

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
        @DisplayName("initialCommand")
        class InitialCommandTest {

            @Mock
            Statement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getInitialCommand();
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).close();
                Mockito.verify(mockStatement).execute("""
                        CREATE TABLE IF NOT EXISTS products (
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
                SqlProcedure command = mapper.getInitialCommand();
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
                SqlProcedure command = mapper.getInitialCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).createStatement();
            }
        }

        @Nested
        @DisplayName("ProductWithHighestIdCommandTest")
        class ProductWithHighestIdCommandTest {

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
                SqlFunction command = mapper.getProductWithHighestIdCommand();
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products ORDER BY id DESC LIMIT 1;");
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
                SqlFunction command = mapper.getProductWithHighestIdCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products ORDER BY id DESC LIMIT 1;");
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                SqlFunction command = mapper.getProductWithHighestIdCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products ORDER BY id DESC LIMIT 1;");
            }
        }

        @Nested
        @DisplayName("ReadCommand")
        class ReadCommandTest {

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
                SqlFunction command = mapper.getReadCommand(12345L);
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products WHERE id=?;");
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
                SqlFunction command = mapper.getReadCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products WHERE id=?;");
                Mockito.verify(mockStatement).executeQuery();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                SqlFunction command = mapper.getReadCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM products WHERE id=?;");
            }
        }

        @Nested
        @DisplayName("DeleteCommand")
        class DeleteCommandTest {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getDeleteCommand(12345L);
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM products WHERE id=?;");
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
                SqlProcedure command = mapper.getDeleteCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM products WHERE id=?;");
                Mockito.verify(mockStatement).executeUpdate();
                Mockito.verify(mockStatement).close();
            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                SqlProcedure command = mapper.getDeleteCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM products WHERE id=?;");
            }
        }

        @Nested
        @DisplayName("InsertCommand")
        class InsertCommandTest {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getInsertCommand(defaultProduct());
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO products VALUES (?, ?);");
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
                SqlProcedure command = mapper.getInsertCommand(defaultProduct());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO products VALUES (?, ?);");
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
                SqlProcedure command = mapper.getInsertCommand(defaultProduct());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO products VALUES (?, ?);");
            }
        }

        @Nested
        @DisplayName("UpdateNameCommand")
        class UpdateNameCommandTest {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getUpdateNameCommand(defaultProduct());
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE products SET name=? WHERE id=?;");
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
                SqlProcedure command = mapper.getUpdateNameCommand(defaultProduct());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE products SET name=? WHERE id=?;");
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
                SqlProcedure command = mapper.getUpdateNameCommand(defaultProduct());
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("UPDATE products SET name=? WHERE id=?;");
            }
        }
    }
}