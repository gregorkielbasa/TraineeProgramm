package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.model.Basket;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;

import static org.lager.BasketFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basket SQL Mapper")
class BasketSqlMapperTest implements WithAssertions {
    BasketSqlMapper mapper = new BasketSqlMapper();

    @Nested
    @DisplayName("decodes ResultSet")
    class SqlDecoderTest {

        @Mock
        ResultSet mockResultSet;

        @Test
        @DisplayName("but answer is empty")
        void emptyCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(false);

            //When
            SqlDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();
            Optional<Basket> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("and returns default Basket")
        void properCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
            Mockito.when(mockResultSet.getLong("customer_id")).thenReturn(defaultCustomerId());
            Mockito.when(mockResultSet.getLong("product_id")).thenReturn(defaultProductId());
            Mockito.when(mockResultSet.getInt("amount")).thenReturn(1);

            //When
            SqlDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();
            Optional<Basket> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet, Mockito.times(2)).next();
            assertThat(result).isEqualTo(Optional.of(defaultBasket()));
        }

        @Test
        @DisplayName("and things go wrong")
        void serverError() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true);
            Mockito.when(mockResultSet.getLong("customer_id")).thenReturn(defaultCustomerId());
            Mockito.when(mockResultSet.getLong("product_id")).thenThrow(SQLException.class);

            //When
            SqlDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();
            Optional<Basket> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            assertThat(result).isEmpty();
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
                Mockito.verify(mockStatement).execute("""
                        CREATE TABLE IF NOT EXISTS basket_items (
                        customer_id bigint NOT NULL,
                        product_id bigint NOT NULL,
                        amount integer NOT NULL,
                        PRIMARY KEY (customer_id, product_id)
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
        @DisplayName("ReadWholeBasketCommandTest")
        class ReadWholeBasketCommandTest {

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
                SqlFunction command = mapper.getReadWholeBasketCommand(12345L);
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM basket_items WHERE customer_id=?;");
                Mockito.verify(mockStatement).setLong(1, 12345L);
                Mockito.verify(mockStatement).executeQuery();

                assertThat(result).isEqualTo(mockResultSet);
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery()).thenThrow(SQLException.class);

                //When
                SqlFunction command = mapper.getReadWholeBasketCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM basket_items WHERE customer_id=?;");
                Mockito.verify(mockStatement).executeQuery();

            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                SqlFunction command = mapper.getReadWholeBasketCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("SELECT * FROM basket_items WHERE customer_id=?;");
            }
        }

        @Nested
        @DisplayName("DeleteWholeBasketCommandTest")
        class DeleteWholeBasketCommandTest {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getDeleteWholeBasketCommand(12345L);
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM basket_items WHERE customer_id=?;");
                Mockito.verify(mockStatement).setLong(1, 12345L);
                Mockito.verify(mockStatement).executeUpdate();

            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeUpdate()).thenThrow(SQLException.class);

                //When
                SqlProcedure command = mapper.getDeleteWholeBasketCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM basket_items WHERE customer_id=?;");
                Mockito.verify(mockStatement).executeUpdate();

            }

            @Test
            @DisplayName("and throws Exception during creation of Statement")
            void exceptionDuringStatementCreation() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenThrow(SQLException.class);

                //When
                SqlProcedure command = mapper.getDeleteWholeBasketCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("DELETE FROM basket_items WHERE customer_id=?;");
            }
        }

        @Nested
        @DisplayName("InsertWholeBasketCommandsTest")
        class InsertWholeBasketCommandsTest {

            @Mock
            PreparedStatement mockStatement;
            @Mock
            SqlProcedure command1;
            @Mock
            SqlProcedure command2;

            @Test
            @DisplayName("")
            void insertEmptyBasket() {
                //Given

                //When
                SqlProcedure[] commands = mapper.getInsertWholeBasketCommands(defaultEmptyBasket());

                //Then
                assertThat(commands).isEmpty();
            }

            @Test
            @DisplayName("and works")
            void insertBiggerBasket() {
                //Given
                BasketSqlMapper mockMapper = spy(mapper);
                Mockito.when(mockMapper.getInsertBasketItemCommand(anyLong(), anyLong(), anyInt()))
                        .thenReturn(command1).thenReturn(command2);

                //When
                SqlProcedure[] commands = mockMapper.getInsertWholeBasketCommands(anotherBasket());

                //Then
                assertThat(commands).containsExactly(command1, command2);
                Mockito.verify(mockMapper).getInsertBasketItemCommand(anotherCustomerId(), defaultProductId(), 2);
                Mockito.verify(mockMapper).getInsertBasketItemCommand(anotherCustomerId(), anotherProductId(), 3);
            }
        }

        @Nested
        @DisplayName("InsertBasketItemCommandTest")
        class InsertBasketItemCommandTest {

            @Mock
            PreparedStatement mockStatement;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getInsertBasketItemCommand(123, 456, 789);
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO basket_items VALUES (?, ?, ?);");
                Mockito.verify(mockStatement).setLong(1, 123);
                Mockito.verify(mockStatement).setLong(2, 456);
                Mockito.verify(mockStatement).setInt(3, 789);
                Mockito.verify(mockStatement).executeUpdate();
            }
        }
    }
}