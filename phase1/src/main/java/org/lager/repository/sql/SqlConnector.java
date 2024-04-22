package org.lager.repository.sql;

import org.lager.exception.SqlConnectorException;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;
import java.util.function.Supplier;

public class SqlConnector<T> {

    private final static Logger logger = LoggerFactory.getLogger(SqlConnector.class);
    Supplier<Connection> connectionSupplier;

    public SqlConnector(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    //SELECT
    public Optional<T> receiveFromDB(CommandQuery command, ResultSetDecoder<Optional<T>> mapper) {
        try (Connection connection = connectionSupplier.get()) {
            try (ResultSet result = command.execute(connection)) {
                return mapper.decode(result);
            } catch (SQLException e) {
                logger.error("SQL Connector could not execute CommandQuery!\n{}", e.getMessage());
                throw new SqlConnectorException("CommandQuery was not able to be executed", e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectorException("Connection has been broken", e.getMessage());
        }
    }

    //INSERT //UPDATE
    public void sendToDB(CommandUpdate... commands) throws SqlConnectorException {
        try (Connection connection = connectionSupplier.get()) {
            connection.setAutoCommit(false);
            try {
                for (CommandUpdate command : commands)
                    command.execute(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("SQL Connector could not execute CommandQuery!\n{}", e.getMessage());
                throw new SqlConnectorException("CommandUpdate was not able to be executed", e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectorException("Connection has been broken", e.getMessage());
        }
    }
}
