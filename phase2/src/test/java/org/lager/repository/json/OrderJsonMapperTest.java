package org.lager.repository.json;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.model.Order;

import java.util.List;
import java.util.Optional;

import static org.lager.OrderFixtures.*;

@DisplayName("Order JSON ObjectMapper")
class OrderJsonMapperTest implements WithAssertions {

    OrderJsonMapper jsonMapper = new OrderJsonMapper();

    @Nested
    @DisplayName("reads JSON Record")
    class OrderJsonRead {

        @Test
        @DisplayName("NULL record")
        void nullRecord() {
            JsonOrder input = null;
            Optional<Order> output = jsonMapper.jsonRecordToOrder(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("a record with NULL Date")
        void nullDate() {
            JsonOrder input = new JsonOrder(defaultId(), defaultCustomerId(), null, defaultItemsList());
            Optional<Order> output = jsonMapper.jsonRecordToOrder(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("a record with NULL Order Items List")
        void nullItemsList() {
            JsonOrder input = new JsonOrder(defaultId(), defaultCustomerId(), orderDate(), null);
            Optional<Order> output = jsonMapper.jsonRecordToOrder(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("a record with empty Order Items List")
        void emptyItemsList() {
            JsonOrder input = new JsonOrder(defaultId(), defaultCustomerId(), orderDate(), List.of());
            Optional<Order> output = jsonMapper.jsonRecordToOrder(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("a proper JSON")
        void properCase() {
            JsonOrder input = defaultOrderAsJson();
            Optional<Order> output = jsonMapper.jsonRecordToOrder(input);

            assertThat(output).isEqualTo(Optional.of(defaultOrder()));
        }
    }

    @Nested
    @DisplayName("writes JSON Record")
    class OrderJsonWrite {

        @Test
        @DisplayName("NULL record")
        void nullRecord() {
            Order input = null;
            Optional<JsonOrder> output = jsonMapper.orderToJsonRecord(input);

            assertThat(output).isEmpty();
        }

        @Test
        @DisplayName("a proper Order")
        void properCase() {
            Order input = defaultOrder();
            Optional<JsonOrder> output = jsonMapper.orderToJsonRecord(input);

            assertThat(output).isEqualTo(Optional.of(defaultOrderAsJson()));
        }
    }
}