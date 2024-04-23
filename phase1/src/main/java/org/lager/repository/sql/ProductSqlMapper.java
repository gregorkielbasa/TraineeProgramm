package org.lager.repository.sql;

import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.Product;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ProductSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(ProductSqlMapper.class);

    public ResultSetDecoder<Optional<Product>> getResultSetDecoder() {
        return resultSet -> {
            try {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");

                    Product newProduct = new Product(id, name);
                    return Optional.of(newProduct);
                }
            } catch (SQLException e) {
                logger.warn("Product SQL Mapper was not able to decode Product");
            } catch (ProductIllegalIdException | ProductIllegalNameException e) {
                logger.warn("Product SQL Mapper was not able to create a new Product");
            }
            return Optional.empty();
        };
    }

    public CommandUpdate getInitialCommand() {
        return connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS products (
                        id bigint PRIMARY KEY,
                        name character varying(24) NOT NULL
                        );""");
            }
        };
    }

    public CommandQuery getProductWithHighestIdCommand() {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM test ORDER BY id DESC LIMIT 1;")) {
                return statement.executeQuery();
            }
        };
    }

    public CommandQuery getReadCommand(Long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM Products WHERE id=?;")) {
                statement.setLong(1, id);
                return statement.executeQuery();
            }
        };
    }

    public CommandUpdate getDeleteCommand(Long id) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM products WHERE id=?;")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        };
    }

    public CommandUpdate getInsertCommand(Product product) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO Products VALUES (?, ?);")) {
                statement.setLong(1, product.getId());
                statement.setString(2, product.getName());
                statement.executeUpdate();
            }
        };
    }

    public CommandUpdate getUpdateNameCommand(Product product) {
        return connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE Products SET name=? WHERE id=?;")) {
                statement.setString(1, product.getName());
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
        };
    }
}
