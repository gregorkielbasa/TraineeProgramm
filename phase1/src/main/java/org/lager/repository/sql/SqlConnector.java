package org.lager.repository.sql;

import org.lager.exception.SqlCommandException;
import org.lager.exception.SqlConnectionException;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.ConnectionSupplier;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class SqlConnector<T> {

    private final static Logger logger = LoggerFactory.getLogger(SqlConnector.class);
    ConnectionSupplier connectionSupplier;

    public SqlConnector(ConnectionSupplier connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    //SELECT
    public Optional<T> receiveFromDB(CommandQuery command, ResultSetDecoder<Optional<T>> decoder) {
        try (Connection connection = connectionSupplier.get()) {
            try (ResultSet result = command.execute(connection)) {
                return decoder.decode(result);
            } catch (SQLException e) {
                logger.error("SQL Connector could not execute CommandQuery!\n{}", e.getMessage());
                throw new SqlCommandException(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectionException(e.getMessage());
        }
    }

    //INSERT //UPDATE
    public void sendToDB(CommandUpdate... commands) throws SqlCommandException {
        try (Connection connection = connectionSupplier.get()) {
            connection.setAutoCommit(false);
            try {
                for (CommandUpdate command : commands)
                    command.execute(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("SQL Connector could not execute CommandUpdate!\n{}", e.getMessage());
                throw new SqlCommandException(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectionException(e.getMessage());
        }
    }
}
