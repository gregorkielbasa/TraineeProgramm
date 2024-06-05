package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchProductException;
import org.lager.model.dto.ProductDto;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

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
    @DisplayName("creates")
    public void testCreateProduct() {
        //When
        assertThatThrownBy(() -> service.validatePresence(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);
        ProductDto product = service.create(defaultProductName());

        //Then
        assertThat(product).isEqualTo(new ProductDto(defaultProduct()));
    }

    @Test
    @DisplayName("validates presence")
    void validPresenceTest() {
        //Given
        assertThatThrownBy(() -> service.validatePresence(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);
        ProductDto product = service.create(defaultProductName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultProductId()));
        assertThatThrownBy(() -> service.validatePresence(anotherProductId()))
                .isInstanceOf(NoSuchProductException.class);
    }

    @Test
    @DisplayName("deletes existing")
    void deletesExisting() {
        //Given
        ProductDto product = service.create(defaultProductName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultProductId()));
        service.delete(defaultProductId());
        assertThatThrownBy(() -> service.validatePresence(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);

        //Then
        assertThat(product).isEqualTo(new ProductDto(defaultProduct()));
    }

    @Test
    @DisplayName("deletes non existing")
    void deletesNonExisting() {
        //Given

        //When
        assertThatThrownBy(() -> service.get(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);
        service.delete(defaultProductId());
        assertThatThrownBy(() -> service.get(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);

        //Then
    }

    @Test
    @DisplayName("renames existing")
    void renamesExisting() {
        //Given
        service.create(defaultProductName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultProductId()));
        service.rename(defaultProductId(), "new Name");
        ProductDto product = service.get(defaultProductId());

        //Then
        assertThat(product).isEqualTo(new ProductDto(defaultProductWithName("new Name")));
    }

    @Test
    @DisplayName("renames non existing")
    void renamesNonExisting() {
        //Given

        //When
        assertThatThrownBy(() -> service.get(defaultProductId()))
                .isInstanceOf(NoSuchProductException.class);
        assertThatThrownBy(() -> service.rename(defaultProductId(), "new Name"))
                .isInstanceOf(NoSuchProductException.class);

        //Then
    }
}