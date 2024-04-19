package org.lager.repository.sql;

import org.lager.exception.SqlConnectorException;
import org.lager.model.Customer;

import java.sql.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SqlConnector <T>{

    private final Supplier<Connection> connectionSupplier;

    public SqlConnector(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    //SELECT
    public Optional<T> receiveFromDB(Function<ResultSet, Optional<T>> mapper, String query) throws SqlConnectorException{
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {

            return mapper.apply(result);

        } catch (Exception e) {
            throw new SqlConnectorException(e.getMessage(), query);
        }
    }

    //INSERT //UPDATE
    public void sendToDB(Consumer<Connection> command) throws SqlConnectorException {
        try (Connection connection = connectionSupplier.get()){

            command.accept(connection);
        } catch (Exception e) {
            throw new SqlConnectorException(e.getMessage());
        }
    }

    //DELETE //CREATE
    public void sendToDB(String... queries) throws SqlConnectorException {
        try (Connection connection = connectionSupplier.get();
             Statement statement = connection.createStatement();
             AutoCloseable finish = connection::rollback) {

            connection.setAutoCommit(false);

            for (String query : queries)
                statement.execute(query);

            connection.commit();
            //finish.close();
        } catch (Exception e) {
            throw new SqlConnectorException(e.getMessage(), queries);
        }
    }
}
