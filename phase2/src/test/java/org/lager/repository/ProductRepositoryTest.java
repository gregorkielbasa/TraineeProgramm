package org.lager.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.lager.ProductFixtures.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Product Repository")
class ProductRepositoryTest implements WithAssertions {

    @Autowired
    ProductRepository repository;

    @Test
    @DisplayName("saves and reads two Products")
    void saveAndRead() {
        //Given
        repository.save(defaultProduct());
        repository.save(anotherProduct());

        //When
        Optional<Product> expected1 = repository.findById(defaultProductId());
        Optional<Product> expected2 = repository.findById(anotherProductId());

        //Then
        assertThat(expected1).isEqualTo(Optional.of(defaultProduct()));
        assertThat(expected2).isEqualTo(Optional.of(anotherProduct()));
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
        @DisplayName("but first saves two products")
        void savesAndReadsList() {
            //Given
            repository.save(defaultProduct());
            repository.save(anotherProduct());
            List<Long> expected = List.of(defaultProductId(), anotherProductId());

            // When
            List<Long> result = repository.getAllIds();

            //Then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("deletes one Product and calls getAllIds")
    void delete() {
        //Given
        repository.save(defaultProduct());
        repository.save(anotherProduct());
        List<Long> expected = List.of(anotherProductId());

        //When
        repository.deleteById(defaultProductId());
        List<Long> result = repository.getAllIds();

        //Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("renames one Product")
    void rename() {
        //Given
        repository.save(defaultProduct());

        //When
        repository.save(defaultProductWithName("newName"));
        Optional<Product> expected = repository.findById(defaultProductId());

        //Then
        assertThat(expected).isEqualTo(Optional.of(defaultProductWithName("newName")));
    }
}