package org.lager.repository.sql;

import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(OrderSqlMapper.class);

    public SqlDecoder<Optional<Order>> getResultSetDecoder() {
        return resultSet -> {
            try {
                List<OrderItem> items = new ArrayList<>();
                long orderId = 0;
                long customerId = 0;
                LocalDateTime dateTime = null;

                while (resultSet.next()) {
                    long productId = resultSet.getLong("product_id");
                    int productAmount = resultSet.getInt("amount");
                    orderId = resultSet.getLong("order_id");
                    customerId = resultSet.getLong("customer_id");
                    dateTime = resultSet.getTimestamp("dateTime").toLocalDateTime();

                    items.add(new OrderItem(productId, productAmount));
                }

                if (items.isEmpty())
                    return Optional.empty();

                Order newOrder = new Order(orderId, customerId, dateTime, items);
                return Optional.of(newOrder);
            } catch (SQLException e) {
                logger.warn("Order SQL Mapper was not able to decode Order");
            }
            return Optional.empty();
        };
    }

    public SqlProcedure getOrderInitialCommand() {
        return connection -> {
            String command = """
                    CREATE TABLE IF NOT EXISTS orders (
                    order_id bigint PRIMARY KEY,
                    customer_id bigint NOT NULL,
                    dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL
                    );""";
            Statement statement = connection.createStatement();
            statement.execute(command);
        };
    }

    public SqlProcedure getOrderItemInitialCommand() {
        return connection -> {
            String command = """
                    CREATE TABLE IF NOT EXISTS order_items (
                    order_id bigint NOT NULL,
                    product_id bigint NOT NULL,
                    amount integer NOT NULL,
                    PRIMARY KEY (order_id, product_id)
                    );""";
            Statement statement = connection.createStatement();
            statement.execute(command);
        };
    }

    public SqlFunction getOrderWithHighestIdCommand() {
        return connection -> {
            String command = """
                    SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                    FROM orders
                    INNER JOIN order_items
                    ON orders.order_id=order_items.order_id
                    ORDER BY order_id DESC LIMIT 1;""";
            Statement statement = connection.createStatement();
            return statement.executeQuery(command);
        };
    }

    public SqlFunction getReadCommand(long id) {
        return connection -> {
            String command = """
                    SELECT orders.order_id, orders.customer_id, orders.dateTime, order_items.product_id, order_items.amount
                    FROM orders
                    INNER JOIN order_items
                    ON orders.order_id=order_items.order_id
                    WHERE orders.order_id=?;""";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteAllOrderItemsCommand(long id) {
        return connection -> {
            String command = "DELETE FROM order_items WHERE order_id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            statement.executeUpdate();
        };
    }

    public SqlProcedure[] getInsertOrderCommands(Order order) {
        ArrayList<SqlProcedure> commandQueue = new ArrayList<>();

        commandQueue.add(getInsertEmptyOrderCommand(order));
        order.getItems().stream()
                .map(item -> getInsertOrderItemCommand(order.getId(), item.productId(), item.amount()))
                .forEach(commandQueue::add);

        return commandQueue.toArray(new SqlProcedure[0]);
    }

    private SqlProcedure getInsertEmptyOrderCommand(Order order) {
        return connection -> {
            String command = "INSERT INTO orders VALUES (?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, order.getId());
            statement.setLong(2, order.getCustomerId());
            statement.setTimestamp(3, Timestamp.valueOf(order.getDateTime()));
            statement.executeUpdate();
        };
    }

    private SqlProcedure getInsertOrderItemCommand(long orderId, long productId, int amount) {
        return connection -> {
            String command = "INSERT INTO order_items VALUES (?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, orderId);
            statement.setLong(2, productId);
            statement.setInt(3, amount);
            statement.executeUpdate();
        };
    }
}
