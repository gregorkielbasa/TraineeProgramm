package org.lager.repository.sql;

import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.model.Product;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ProductSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(ProductSqlMapper.class);

    public SqlDecoder<Optional<Product>> getResultSetDecoder() {
        return resultSet -> {
            try {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");

                    Product newProduct = new Product(id, name);
                    return Optional.of(newProduct);
                }
            } catch (SQLException e) {
                logger.warn("Product SQL Mapper was not able to decode Product{}", e.getMessage());
            } catch (ProductIllegalIdException | ProductIllegalNameException e) {
                logger.warn("Product SQL Mapper was not able to create a new Product{}", e.getMessage());
            }
            return Optional.empty();
        };
    }

    public SqlProcedure getInitialCommand() {
        return connection -> {
            String command = """
                    CREATE TABLE IF NOT EXISTS products (
                    id bigint PRIMARY KEY,
                    name character varying(24) NOT NULL
                    );""";
            Statement statement = connection.createStatement();
            statement.execute(command);
        };
    }

    public SqlFunction getProductWithHighestIdCommand() {
        return connection -> {
            String command = "SELECT * FROM products ORDER BY id DESC LIMIT 1;";
            Statement statement = connection.createStatement();
            return statement.executeQuery(command);
        };
    }

    public SqlFunction getReadCommand(Long id) {
        return connection -> {
            String command = "SELECT * FROM products WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteCommand(Long id) {
        return connection -> {
            String command = "DELETE FROM products WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            statement.executeUpdate();
        };
    }

    public SqlProcedure getInsertCommand(Product product) {
        return connection -> {
            String command = "INSERT INTO products VALUES (?, ?);";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, product.getId());
            statement.setString(2, product.getName());
            statement.executeUpdate();
        };
    }

    public SqlProcedure getUpdateNameCommand(Product product) {
        return connection -> {
            String command = "UPDATE products SET name=? WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setString(1, product.getName());
            statement.setLong(2, product.getId());
            statement.executeUpdate();
        };
    }
}
