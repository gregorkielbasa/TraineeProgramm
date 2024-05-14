package org.lager.repository.sql;

import org.lager.model.Basket;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Component
@Profile("database")
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
            String command = """
                    CREATE TABLE IF NOT EXISTS basket_items (
                    customer_id bigint NOT NULL,
                    product_id bigint NOT NULL,
                    amount integer NOT NULL,
                    PRIMARY KEY (customer_id, product_id)
                    );""";
            Statement statement = connection.createStatement();
            statement.execute(command);
        };
    }

    public SqlFunction getReadWholeBasketCommand(long id) {
        return connection -> {
            String command = "SELECT * FROM basket_items WHERE customer_id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            return statement.executeQuery();
        };
    }

    public SqlProcedure getDeleteWholeBasketCommand(long id) {
        return connection -> {
            String command = "DELETE FROM basket_items WHERE customer_id=?;";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, id);
            statement.executeUpdate();
        };
    }

    public SqlProcedure[] getInsertWholeBasketCommands(Basket basket) {
        return basket.getContent().entrySet().stream()
                .map(entry -> getInsertBasketItemCommand(basket.getCustomerId(), entry.getKey(), entry.getValue()))
                .toList().toArray(new SqlProcedure[0]);
    }

    public SqlProcedure getInsertBasketItemCommand(long customerId, long productId, int amount) {
        return connection -> {
            String command = "INSERT INTO basket_items VALUES (?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.setLong(1, customerId);
            statement.setLong(2, productId);
            statement.setInt(3, amount);
            statement.executeUpdate();
        };
    }
}
