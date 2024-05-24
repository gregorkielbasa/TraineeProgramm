package org.lager.model;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;

import java.util.LinkedHashMap;
import java.util.Map;


@DisplayName("Basket")
class BasketTest implements WithAssertions {

    @DisplayName("when is empty")
    @Nested
    class BasketTestEmpty {

        Basket basket;

        @BeforeEach
        void init() {
            basket = new Basket(123_123_123);
        }

        @Test
        @DisplayName("should return an empty map and right customer ID")
        void getAllEmpty() {
            assertThat(basket.getContent()).isEmpty();
            assertThat(basket.getCustomerId()).isEqualTo(123_123_123L);
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

    @Nested
    @DisplayName("tests equality")
    class EqualityBasket {

        @Test
        @DisplayName("of the same Basket")
        void theSameObject() {
            Basket basket = new Basket(123_456_789L);
            basket.insert(100_000_000, 1);

            assertThat(basket.equals(basket)).isTrue();
        }

        @Test
        @DisplayName("of NULL")
        void nullBasket() {
            Basket basket = new Basket(123_456_789L);
            basket.insert(100_000_000, 1);

            assertThat(basket.equals(null)).isFalse();
        }

        @Test
        @DisplayName("of different Classes")
        void differentClasses() {
            Basket basket = new Basket(123_456_789L);
            basket.insert(100_000_000, 1);

            assertThat(basket.equals("any")).isFalse();
        }

        @Test
        @DisplayName("of two the same Baskets")
        void similarBasket() {
            Basket basket1 = new Basket(123_456_789L);
            basket1.insert(100_000_000, 1);

            Basket basket2 = new Basket(123_456_789L);
            basket2.insert(100_000_000, 1);

            assertThat(basket1.equals(basket2)).isTrue();
        }

        @Test
        @DisplayName("of two different Baskets")
        void differentIdBasket() {
            Basket basket1 = new Basket(123_456_789L);
            basket1.insert(100_000_000, 1);

            Basket basket2 = new Basket(123_123_123L);
            basket2.insert(100_000_000, 1);

            assertThat(basket1.equals(basket2)).isFalse();
        }

        @Test
        @DisplayName("of two the same Baskets with different Items")
        void differentItems() {
            Basket basket1 = new Basket(123_456_789L);
            basket1.insert(123_123_123, 1);

            Basket basket2 = new Basket(123_456_789L);
            basket2.insert(100_000_000, 1);

            assertThat(basket1.equals(basket2)).isFalse();
        }
    }

    @Nested
    @DisplayName("tests its hashCode")
    class HashCodeBasket {

        Basket basket1 = new Basket(123_123_123L);
        Basket basket2 = new Basket(123_123_123L);
        Basket basket3 = new Basket(123_456_789L);

        @Test
        @DisplayName("and they should be the same")
        void similarBasket() {
            basket1.insert(100_000_000L, 1);
            basket2.insert(100_000_000L, 1);

            assertThat(basket1.hashCode())
                    .isEqualTo(basket2.hashCode());
        }

        @Test
        @DisplayName("with different names")
        void differentNameBasket() {
            basket1.insert(100_000_000L, 1);
            basket3.insert(100_000_000L, 1);

            assertThat(basket1.hashCode())
                    .isNotEqualTo(basket3.hashCode());
        }

        @Test
        @DisplayName("with different items")
        void sameNameDifferentItems() {
            basket1.insert(100_000_000L, 1);
            basket2.insert(123_123_123, 1);

            assertThat(basket1.hashCode())
                    .isNotEqualTo(basket3.hashCode());
        }
    }
}