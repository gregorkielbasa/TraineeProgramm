package org.lager.repository.sql;

import org.lager.model.Order;
import org.lager.model.OrderItem;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
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

    public ResultSetDecoder<Optional<Order>> getResultSetDecoder() {
        return resultSet -> {
            try {
                List<OrderItem> items = new ArrayList<>();

                while (resultSet.next()) {
                    long productId = resultSet.getLong("product_id");
                    int productAmount = resultSet.getInt("amount");

                    items.add(new OrderItem(productId, productAmount));
                }

                if (items.isEmpty())
                    return Optional.empty();

                long orderId = resultSet.getLong("order_id");
                long customerId = resultSet.getLong("customer_id");
                LocalDateTime dateTime = resultSet.getTimestamp("dateTime").toLocalDateTime();
                Order newOrder = new Order(orderId, customerId, dateTime, items);
                return Optional.of(newOrder);
            } catch (SQLException e) {
                logger.warn("Order SQL Mapper was not able to decode Order");
            }
            return Optional.empty();
        };
    }

    public CommandUpdate getOrderInitialCommand() {
        return connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS orders (
                        order_id bigint PRIMARY KEY,
                        customer_id bigint NOT NULL,
                        dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL
                        );""");
            }
        };
    }

    public CommandUpdate getOrderItemInitialCommand() {
        return connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS order_items (
                        order_id bigint PRIMARY KEY,
                        product_id bigint NOT NULL,
                        amount integer NOT NULL,
                        PRIMARY KEY (order_id, product_id)
                        );""");
            }
        };
    }

    public CommandQuery getOrderWithHighestIdCommand() {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("""
                            SELECT orders.order_id, orders.customer_id, orders.dateTime, order_times.product_id, order_times.amount 
                            FROM orders
                            INNER JOIN order_items
                            ON orders.order_id=order_items.order_id
                            ORDER BY order_id DESC LIMIT 1;""")) {
                return statement.executeQuery();
            }
        };
    }

    public CommandQuery getReadCommand(long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("""
                            SELECT orders.order_id, orders.customer_id, orders.dateTime, order_times.product_id, order_times.amount 
                            FROM orders
                            INNER JOIN order_items
                            ON orders.order_id=order_items.order_id
                            WHERE orders.order_id=?;""")) {
                statement.setLong(1, id);
                return statement.executeQuery();
            }
        };
    }

    public CommandUpdate getDeleteAllOrderItemsCommand(long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM order_items WHERE order_id=?;")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        };
    }

    public CommandUpdate getInsertEmptyOrderCommand(Order order) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO orders VALUES (?, ?, ?);")) {
                statement.setLong(1, order.getId());
                statement.setLong(2, order.getCustomerId());
                statement.setTimestamp(3, Timestamp.valueOf(order.getDateTime()));
                statement.executeUpdate();
            }
        };
    }

    public List<CommandUpdate> getInsertOrderItemsListCommand(Order order) {
        return order.getItems().stream()
                .map(item -> getInsertOrderItemCommand(order.getCustomerId(), item.productId(), item.amount()))
                .toList();
    }

    public CommandUpdate getInsertOrderItemCommand(long customerId, long productId, int amount) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO order_items VALUES (?, ?, ?);")) {
                statement.setLong(1, customerId);
                statement.setLong(2, productId);
                statement.setInt(3, amount);
                statement.executeUpdate();
            }
        };
    }
}
