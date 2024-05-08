package org.lager.repository.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lager.model.Basket;
import org.lager.model.Customer;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Basket SQL Repository")
class BasketSqlRepositoryTest {

    BasketSqlRepository repository;
    @Mock
    BasketSqlMapper mockMapper;
    @Mock
    SqlConnector mockConnector;
    @Mock
    CommandUpdate initCommand;

    @BeforeEach
    void init() {
        Mockito.when(mockMapper.getInitialCommand()).thenReturn(initCommand);

        repository = new BasketSqlRepository(mockMapper, mockConnector);

        Mockito.verify(mockConnector).sendToDB(initCommand);
    }

    @Nested
    @DisplayName("executes Read")
    class read {

        Basket basket = new Basket(123L);

    }

    @Nested
    @DisplayName("executes Delete")
    class delete {
    }

    @Nested
    @DisplayName("executes Save")
    class save {
    }
}

