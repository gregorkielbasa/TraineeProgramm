package org.lager.repository.sql;

import org.lager.exception.SqlCommandException;
import org.lager.exception.SqlConnectionException;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Profile("database")
public class SqlConnector {

    private final static Logger logger = LoggerFactory.getLogger(SqlConnector.class);
    ConnectionSupplier connectionSupplier;

    public SqlConnector(ConnectionSupplier connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    //SELECT
    public <T> Optional<T> receiveFromDB(SqlFunction command, SqlDecoder<Optional<T>> decoder) {
        try (Connection connection = connectionSupplier.get()) {
            try (ResultSet result = command.execute(connection)) {
                connection.commit();
                return decoder.decode(result);
            } catch (SQLException e) {
                logger.error("SQL Connector failed to execute SQL Function!\n{}", e.getMessage());
                safeRollback(connection);
                throw new SqlCommandException(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectionException(e.getMessage());
        }
    }

    public void sendToDB(SqlProcedure command1, SqlProcedure[] commands2) throws SqlConnectionException {
        SqlProcedure[] combinedCommands = Stream
                .concat(Stream.of(command1), Stream.of(commands2)).toArray(SqlProcedure[]::new);
        sendToDB(combinedCommands);
    }

    //INSERT //UPDATE //DELETE
    public void sendToDB(SqlProcedure... commands) throws SqlCommandException {
        try (Connection connection = connectionSupplier.get()) {
            try {
                for (SqlProcedure command : commands)
                    command.execute(connection);
                connection.commit();
            } catch (SQLException e) {
                logger.error("SQL Connector failed to execute SQL Procedure!\n{}", e.getMessage());
                safeRollback(connection);
                throw new SqlCommandException(e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("SQL Connector broke connection!\n{}", e.getMessage());
            throw new SqlConnectionException(e.getMessage());
        }
    }

    private void safeRollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                logger.debug("SQL Connector rolled back changes");
            } catch (SQLException e) {
                logger.warn("SQL Connector failed to rollback changes!\n{}", e.getMessage());
            }
        }
    }
}
