package org.lager.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.lager.OrderFixtures.*;

@DataJpaTest
@DisplayName("Order Repository calls GetAllIds")
class OrderRepositoryTest implements WithAssertions {

    @Autowired
    OrderRepository repository;

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
    @DisplayName("but first saves two Customers")
    void savesAndReadsList() {
        //Given
        repository.save(defaultNewOrder());
        repository.save(anotherNewOrder());
        List<Long> expected = List.of(defaultOrderId(), anotherOrderId());

        //When
        List<Long> result = repository.getAllIds();

        //Then
        assertThat(result).isEqualTo(expected);
    }
}