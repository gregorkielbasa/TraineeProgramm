package org.lager.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.model.Basket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.lager.BasketFixtures.*;
import static org.lager.CustomerFixtures.anotherCustomerId;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.ProductFixtures.anotherProductId;

@DataJpaTest
@DisplayName("Basket Repository")
class BasketRepositoryTest implements WithAssertions {

    @Autowired
    BasketRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM BASKET_ITEMS;");
        jdbcTemplate.execute("DELETE FROM BASKETS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS BASKET_KEY RESTART WITH 1;");
    }

    @Test
    @DisplayName("saves and reads two baskets")
    void findByCustomerId() {
        //Given
        repository.save(defaultNewBasket());
        repository.save(anotherNewBasket());

        //When
        Optional<Basket> expected1 = repository.findByCustomerId(defaultCustomerId());
        Optional<Basket> expected2 = repository.findByCustomerId(anotherCustomerId());

        //Then
        assertThat(expected1).isEqualTo(Optional.of(defaultBasket()));
        assertThat(expected2).isEqualTo(Optional.of(anotherBasket()));
    }

    @Nested
    @DisplayName("calls getAllIds")
    class GetAllIds {

        @Test
        @DisplayName("and should get an empty List")
        void emptyDB() {
            //Given
            List<Long> expected = List.of();

            // When
            List<Long> result = repository.getAllIds();

            //Then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("but first saves two baskets")
        void savesAndReadsList() {
            //Given
            repository.save(defaultNewBasket());
            repository.save(anotherNewBasket());
            List<Long> expected = List.of(defaultCustomerId(), anotherCustomerId());

            // When
            List<Long> result = repository.getAllIds();

            //Then
            assertThat(result).isEqualTo(expected);
        }

    }

    @Test
    @DisplayName("deletes one Basket and calls getAllIds")
    void deleteByCustomerId() {
        //Given
        repository.save(defaultNewBasket());
        repository.save(anotherNewBasket());
        List<Long> expected = List.of(anotherCustomerId());

        // When
        repository.deleteByCustomerId(defaultCustomerId());
        List<Long> result = repository.getAllIds();

        //Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("overrides existing Basket")
    void overridesExisting() {
        //Given
        repository.save(defaultNewBasket());
        repository.save(anotherNewBasket());

        // When
        repository.save(anotherBasketWith(anotherProductId(),3 ));
        Optional<Basket> result = repository.findByCustomerId(anotherCustomerId());

        //Then
        assertThat(result).isEqualTo(Optional.of(anotherBasketWith(anotherProductId(),3 )));
    }
}