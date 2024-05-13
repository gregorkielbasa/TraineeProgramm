package org.lager.repository.sql;

import org.lager.model.Basket;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class BasketSqlMapper {

    private final static Logger logger = LoggerFactory.getLogger(BasketSqlMapper.class);

    public SqlDecoder<Optional<Basket>> getResultSetDecoder() {
        return resultSet -> {
            try {
                if (!resultSet.next())
                    return Optional.empty();

                long customerId = resultSet.getLong("customer_id");
                Basket newBasket = new Basket(customerId);

                do {
                    long productId = resultSet.getLong("product_id");
                    int productAmount = resultSet.getInt("amount");
                    newBasket.insert(productId, productAmount);
                } while (resultSet.next());

                return Optional.of(newBasket);
            } catch (SQLException e) {
                logger.warn("Basket SQL Mapper was not able to decode Basket");
            }
            return Optional.empty();
        };
    }

    public SqlProcedure getInitialCommand() {
        return connection -> {
            Statement statement = connection.createStatement();
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS basket_items (
                        customer_id bigint NOT NULL,
                        product_id bigint NOT NULL,
                        amount integer NOT NULL,
                        PRIMARY KEY (customer_id, product_id)
                        );""");
        };
    }

    public SqlFunction getReadWholeBasketCommand(long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM basket_items WHERE customer_id=?;");
                statement.setLong(1, id);
                return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteWholeBasketCommand(long id) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM basket_items WHERE customer_id=?;");
                statement.setLong(1, id);
                statement.executeUpdate();
        };
    }

    public List<SqlProcedure> getInsertWholeBasketList(Basket basket) {
        return basket.getContent().entrySet().stream()
                .map(entry -> getInsertBasketItemCommand(basket.getCustomerId(), entry.getKey(), entry.getValue()))
                .toList();
    }

    public SqlProcedure getInsertBasketItemCommand(long customerId, long productId, int amount) {
        return connection -> {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO basket_items VALUES (?, ?, ?);");
                statement.setLong(1, customerId);
                statement.setLong(2, productId);
                statement.setInt(3, amount);
                statement.executeUpdate();
        };
    }
}
