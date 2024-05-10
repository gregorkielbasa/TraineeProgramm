package org.lager.repository.sql;

import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.Product;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
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

    public SqlProcedure getInitialCommand() {
        return connection -> {
            Statement statement = connection.createStatement();
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS products (
                        id bigint PRIMARY KEY,
                        name character varying(24) NOT NULL
                        );""");
        };
    }

    public SqlFunction getProductWithHighestIdCommand() {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM products ORDER BY id DESC LIMIT 1;");
                return statement.executeQuery();
        };
    }

    public SqlFunction getReadCommand(Long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM products WHERE id=?;");
                statement.setLong(1, id);
                return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteCommand(Long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM products WHERE id=?;");
                statement.setLong(1, id);
                statement.executeUpdate();
        };
    }

    public SqlProcedure getInsertCommand(Product product) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO products VALUES (?, ?);");
                statement.setLong(1, product.getId());
                statement.setString(2, product.getName());
                statement.executeUpdate();
        };
    }

    public SqlProcedure getUpdateNameCommand(Product product) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("UPDATE products SET name=? WHERE id=?;");
                statement.setString(1, product.getName());
                statement.setLong(2, product.getId());
                statement.executeUpdate();
        };
    }
}
