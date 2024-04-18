package org.lager.repository.sql;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.SqlConnectorException;

import java.sql.ResultSet;
import java.sql.SQLException;

@DisplayName("SQL Connector")
class SqlConnectorTest implements WithAssertions {
    private static final String url = "jdbc:postgresql://localhost:5432/testdb";
    private static final String user = "postgres";
    private static final String password = "pass";
    private final SqlConnector connector = new SqlConnector(url, user, password);

    @BeforeEach
    void emptyDataBase() {
        try {
            connector.saveToDB("DROP TABLE test;");
        } catch (SqlConnectorException ignored) {}

        String query = """
                CREATE TABLE IF NOT EXISTS test (
                id bigint PRIMARY KEY,
                name character varying(5) NOT NULL
                );""";
        connector.saveToDB(query);
    }

//    @AfterAll
//    static void cleanUp() {
//        try {
//            connector.saveToDB("DROP TABLE test;");
//        } catch (SqlConnectorException ignored) {}
//    }

    @Test
    @DisplayName("writes and reads proper case")
    void saveToDB() {
        String query = """
                    INSERT INTO test
                    VALUES (123, 'adam');
                    INSERT INTO test
                    VALUES (999, 'omega');
                    """;
        connector.saveToDB(query);

        String actual;
        try(ResultSet result = connector.loadFromDB("SELECT name FROM test WHERE id=999;")){
            assertThat(result.getString(1)).isEqualTo("omega");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("throws Exception")
    class SqlConnectorThrowsException {

        @Test
        @DisplayName("")
        void loadFromDB() {
        }
    }
}