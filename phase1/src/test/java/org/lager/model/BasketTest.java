package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;

import java.util.LinkedHashMap;
import java.util.Map;


@DisplayName("Basket")
class BasketTest implements WithAssertions {

    @DisplayName("when empty")
    @Nested
    class BasketTestEmpty {

        Basket basket;

        @BeforeEach
        void init() {
            basket = new Basket(123_123_123);
        }

        @Test
        @DisplayName("should return an empty map and right customer number")
        void getAllEmpty() {
            assertThat(basket.getContent()).isEmpty();
            assertThat(basket.getCustomerNumber()).isEqualTo(123_123_123L);
        }

        @Test
        @DisplayName("should add and return two positions")
        void insert() {
            basket.insert(123_123_123, 1);
            basket.insert(321_321_321, 2);

            assertThat(basket.getContent()).containsOnly(
                    Map.entry(321_321_321L, 2),
                    Map.entry(123_123_123L, 1));
        }

        @Test
        @DisplayName("should remove nothing (Non-existing) and return an empty map")
        void removeNonExisting() {
            basket.remove(123_123_123L);
            basket.remove(999_999_999L);

            assertThat(basket.getContent()).isEmpty();
        }
    }

    @DisplayName("when contains 5 elements")
    @Nested
    class BasketTestNotEmpty {
        Basket basket;
        Map<Long, Integer> expected;

        @BeforeEach
        void init() {
            basket = new Basket(123_456_678);
            basket.insert(123_000_001, 1);
            basket.insert(123_000_002, 2);
            basket.insert(123_000_003, 3);
            basket.insert(123_000_004, 4);
            basket.insert(123_000_005, 5);

            expected = new LinkedHashMap<>();
            expected.put(123_000_004L, 4);
            expected.put(123_000_001L, 1);
            expected.put(123_000_005L, 5);
            expected.put(123_000_003L, 3);
            expected.put(123_000_002L, 2);
        }

        @Test
        @DisplayName("should add 6th one")
        void insertNewOne() {
            basket.insert(123_000_006L, 6);
            expected.put(123_000_006L, 6);

            assertThat(basket.getContent()).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        @DisplayName("should add amount of existing one")
        void insertExisting() {
            basket.insert(123_000_005L, 5);
            expected.put(123_000_005L, 10);

            assertThat(basket.getContent()).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        @DisplayName("should add negative amount and remove")
        void insertNegativeAmount() {
            basket.insert(123_000_005L, -5);
            expected.remove(123_000_005L);

            assertThat(basket.getContent()).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        @DisplayName("should do nothing when amount=0")
        void insertZeroAmount() {
            basket.insert(123_000_005L, 0);

            assertThat(basket.getContent()).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        @DisplayName("should remove existing one")
        void removeExisting() {
            basket.remove(123_000_005L);
            expected.remove(123_000_005L);

            assertThat(basket.getContent()).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        @DisplayName("should return amount of existing one")
        void getAmountOfExisting() {
            assertThat(basket.getAmountOf(123_000_005L)).isEqualTo(5);
        }

        @Test
        @DisplayName("should return 0 as an amount of Non-existing one")
        void getAmountOfNonExisting() {
            assertThat(basket.getAmountOf(999_999_999L)).isEqualTo(0);
        }
    }

    @DisplayName("when concats with")
    @Nested
    class BasketTestConcat {

        Basket basket1;
        Basket basket2;

        @BeforeEach
        void init() {
            basket1 = new Basket(123_123_123);
            basket1.insert(123_000_001, 1);
            basket1.insert(123_000_005, 5);

            basket2 = new Basket(123_123_123);
            basket2.insert(123_000_002, 2);
            basket2.insert(123_000_005, 5);
        }

        @Test
        @DisplayName("NULL")
        void insert() {
            basket1.concatWith(null);

            assertThat(basket1.getContent()).containsOnly(
                    Map.entry(123_000_005L, 5),
                    Map.entry(123_000_001L, 1));
        }

        @Test
        @DisplayName("a proper Basket")
        void remove() {
            basket1.concatWith(basket2);

            assertThat(basket1.getContent()).containsOnly(
                    Map.entry(123_000_005L, 10),
                    Map.entry(123_000_002L, 2),
                    Map.entry(123_000_001L, 1));
        }
    }
}