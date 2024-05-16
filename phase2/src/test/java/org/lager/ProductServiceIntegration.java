package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.lager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.lager.ProductFixtures.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:integrationtest.properties")
@ActiveProfiles("database")
class ProductServiceIntegration implements WithAssertions {

    @Autowired
    ProductService service;

    @Test
    void testProductServiceOperations() {

        assertThat(service.search(defaultId())).isEmpty();
        assertThat(service.search(anotherId())).isEmpty();
        service.create("Product One");
        service.create("Product Two");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(defaultProduct()));
        assertThat(service.search(anotherId())).isEqualTo(Optional.of(anotherProduct()));

        service.rename(defaultId(), "Product New Name");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(productWithName("Product New Name")));

        service.delete(defaultId());
        assertThat(service.search(defaultId())).isEmpty();
        assertThat(service.search(anotherId())).isEqualTo(Optional.of(anotherProduct()));
    }
}