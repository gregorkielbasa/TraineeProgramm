package org.lager.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.lager.CustomerFixtures.*;

@DataJpaTest
@DisplayName("Customer Repository calls getAllId")
class CustomerRepositoryTest implements WithAssertions {

    @Autowired
    CustomerRepository repository;

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
        repository.save(defaultNewCustomer());
        repository.save(anotherNewCustomer());
        List<Long> expected = List.of(defaultCustomerId(), anotherCustomerId());

        //When
        List<Long> result = repository.getAllIds();

        //Then
        assertThat(result).isEqualTo(expected);
    }
}