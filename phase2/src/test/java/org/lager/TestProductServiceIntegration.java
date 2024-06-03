package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.lager.ProductFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated ProductService")
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:integrationtest.properties")
class TestProductServiceIntegration implements WithAssertions {

    @Autowired
    ProductService service;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM PRODUCTS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS PRODUCT_KEY RESTART WITH 100000000;");
    }

    @Test
    @DisplayName("creats")
    public void testCreateProduct() {
        //When
        Optional<Product> productBefore = service.search(defaultProductId());
        Product product = service.create(defaultProductName());
        Optional<Product> productAfter = service.search(defaultProductId());

        //Then
        assertThat(productBefore).isEmpty();
        assertThat(product).isEqualTo(defaultProduct());
        assertThat(productAfter).isEqualTo(Optional.of(defaultProduct()));
    }

    @Test
    @DisplayName("validates presence")
    void validPresenceTest() {
        //Given
        Product product = service.create(defaultProductName());

        //When
        service.validatePresence(defaultProductId());
        assertThatThrownBy(() -> service.validatePresence(anotherProductId()))
                .isInstanceOf(NoSuchProductException.class);
    }

    @Test
    @DisplayName("deletes existing")
    void deletesExisting() {
        //Given
        Product product = service.create(defaultProductName());

        //When
        Optional<Product> productBefore = service.search(defaultProductId());
        service.delete(defaultProductId());
        Optional<Product> productAfter = service.search(defaultProductId());

        //Then
        assertThat(productBefore).isEqualTo(Optional.of(defaultProduct()));
        assertThat(product).isEqualTo(defaultProduct());
        assertThat(productAfter).isEmpty();
    }

    @Test
    @DisplayName("deletes non existing")
    void deletesNonExisting() {
        //Given

        //When
        Optional<Product> productBefore = service.search(defaultProductId());
        service.delete(defaultProductId());
        Optional<Product> productAfter = service.search(defaultProductId());

        //Then
        assertThat(productBefore).isEmpty();
        assertThat(productAfter).isEmpty();
    }

    @Test
    @DisplayName("renames existing")
    void renamesExisting() {
        //Given
        Product product = service.create(defaultProductName());

        //When
        Optional<Product> productBefore = service.search(defaultProductId());
        service.rename(defaultProductId(), "new Name");
        Optional<Product> productAfter = service.search(defaultProductId());

        //Then
        assertThat(productBefore).isEqualTo(Optional.of(defaultProduct()));
        assertThat(product).isEqualTo(defaultProduct());
        assertThat(productAfter).isEqualTo(Optional.of(defaultProductWithName("new Name")));
    }

    @Test
    @DisplayName("renames non existing")
    void renamesNonExisting() {
        //Given

        //When
        Optional<Product> productBefore = service.search(defaultProductId());
        assertThatThrownBy(() -> service.rename(defaultProductId(), "new Name"))
                .isInstanceOf(NoSuchProductException.class);

        //Then
        assertThat(productBefore).isEmpty();
    }
}