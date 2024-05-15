package org.lager.repository.sql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.ProductFixtures;
import org.lager.model.Order;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lager.BasketFixtures.defaultProductId;
import static org.lager.OrderFixtures.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order SQL Mapper")
class OrderSqlMapperTest {
    OrderSqlMapper mapper = new OrderSqlMapper();

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
            SqlDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();
            Optional<Order> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("and returns default Basket")
        void properCase() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
            Mockito.when(mockResultSet.getLong("product_id")).thenReturn(defaultProductId());
            Mockito.when(mockResultSet.getInt("amount")).thenReturn(1);
            Mockito.when(mockResultSet.getLong("order_id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getLong("customer_id")).thenReturn(defaultCustomerId());
            Mockito.when(mockResultSet.getTimestamp("dateTime")).thenReturn(Timestamp.valueOf(orderDate()));

            //When
            SqlDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();
            Optional<Order> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet, Mockito.times(2)).next();
            assertThat(result).isEqualTo(Optional.of(defaultOrder()));
        }

        @Test
        @DisplayName("and things go wrong")
        void serverError() throws SQLException {
            //Given
            Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
            Mockito.when(mockResultSet.getLong("product_id")).thenReturn(defaultProductId());
            Mockito.when(mockResultSet.getInt("amount")).thenReturn(1);
            Mockito.when(mockResultSet.getLong("order_id")).thenReturn(defaultId());
            Mockito.when(mockResultSet.getLong("customer_id")).thenReturn(defaultCustomerId());
            Mockito.when(mockResultSet.getTimestamp("dateTime")).thenThrow(SQLException.class);;

            //When
            SqlDecoder<Optional<Order>> decoder = mapper.getResultSetDecoder();
            Optional<Order> result = decoder.decode(mockResultSet);

            //Then
            Mockito.verify(mockResultSet).next();
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
            @DisplayName("and initialise Order Table")
            void properCaseOrder() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getOrderInitialCommand();
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).execute("""
                        CREATE TABLE IF NOT EXISTS orders (
                        order_id bigint PRIMARY KEY,
                        customer_id bigint NOT NULL,
                        dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL
                        );""");
            }

            @Test
            @DisplayName("and initialise Order Item Table")
            void properCaseOrderItem() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);

                //When
                SqlProcedure command = mapper.getOrderItemInitialCommand();
                command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).execute("""
                        CREATE TABLE IF NOT EXISTS order_items (
                        order_id bigint NOT NULL,
                        product_id bigint NOT NULL,
                        amount integer NOT NULL,
                        PRIMARY KEY (order_id, product_id)
                        );""");
            }

            @Test
            @DisplayName("and throws Exception during execution")
            void exceptionDuringExecution() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
                Mockito.doThrow(SQLException.class).when(mockStatement).execute(any());

                //When
                SqlProcedure command = mapper.getOrderInitialCommand();
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
                SqlProcedure command = mapper.getOrderInitialCommand();
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).createStatement();
            }
        }

        @Nested
        @DisplayName("ReadCommandTest")
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
                Mockito.verify(mockConnection).prepareStatement("""
                        SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                        FROM orders
                        INNER JOIN order_items
                        ON orders.order_id=order_items.order_id
                        WHERE orders.order_id=?;""");
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
                SqlFunction command = mapper.getReadCommand(12345L);
                assertThatThrownBy(() -> command.execute(mockConnection))
                        .isInstanceOf(SQLException.class);

                //Then
                Mockito.verify(mockConnection).prepareStatement("""
                        SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                        FROM orders
                        INNER JOIN order_items
                        ON orders.order_id=order_items.order_id
                        WHERE orders.order_id=?;""");
                Mockito.verify(mockStatement).executeQuery();

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
                Mockito.verify(mockConnection).prepareStatement("""
                        SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                        FROM orders
                        INNER JOIN order_items
                        ON orders.order_id=order_items.order_id
                        WHERE orders.order_id=?;""");
            }
        }

        @Nested
        @DisplayName("OrderWithHighestIdCommand")
        class OrderWithHighestIdCommandTest {

            @Mock
            Statement mockStatement;
            @Mock
            ResultSet mockResultSet;

            @Test
            @DisplayName("and works")
            void properCase() throws SQLException {
                //Given
                Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
                Mockito.when(mockStatement.executeQuery(any())).thenReturn(mockResultSet);

                //When
                SqlFunction command = mapper.getOrderWithHighestIdCommand();
                ResultSet result = command.execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).createStatement();
                Mockito.verify(mockStatement).executeQuery("""
                    SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                    FROM orders
                    INNER JOIN order_items
                    ON orders.order_id=order_items.order_id
                    ORDER BY order_id DESC LIMIT 1;""");

                assertThat(result).isEqualTo(mockResultSet);
            }
        }

        @Nested
        @DisplayName("InsertWholeBasketCommandsTest")
        class InsertWholeBasketCommandsTest {

            @Mock
            PreparedStatement mockStatement1;
            @Mock
            PreparedStatement mockStatement2;

            @Test
            @DisplayName("and works")
            void insertOrder() throws SQLException {
                //Given
                Mockito.when(mockConnection.prepareStatement(any()))
                        .thenReturn(mockStatement1)
                        .thenReturn(mockStatement2);

                //When
                SqlProcedure[] commands = mapper.getInsertOrderCommands(defaultOrder());
                commands[0].execute(mockConnection);
                commands[1].execute(mockConnection);

                //Then
                Mockito.verify(mockConnection).prepareStatement("INSERT INTO orders VALUES (?, ?, ?);");
                Mockito.verify(mockStatement1).setLong(1, defaultId());
                Mockito.verify(mockStatement1).setLong(2, defaultCustomerId());
                Mockito.verify(mockStatement1).setTimestamp(3, Timestamp.valueOf(orderDate()));
                Mockito.verify(mockStatement1).executeUpdate();

                Mockito.verify(mockConnection).prepareStatement("INSERT INTO order_items VALUES (?, ?, ?);");
                Mockito.verify(mockStatement2).setLong(1, defaultId());
                Mockito.verify(mockStatement2).setLong(2, ProductFixtures.defaultId());
                Mockito.verify(mockStatement2).setInt(3, 1);
                Mockito.verify(mockStatement2).executeUpdate();
            }
        }
    }
}