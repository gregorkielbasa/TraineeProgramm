package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.lager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:integrationtest.properties")
@ActiveProfiles("database")
class TestCustomerServiceIntegration implements WithAssertions {

    @Autowired
    CustomerService service;

    @Test
    void testCustomerServiceOperations() {

        assertThat(service.search(defaultId())).isEmpty();
        service.create("customerOne");
        service.create("customerTwo");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(defaultCustomer()));
        assertThat(service.search(anotherId())).isEqualTo(Optional.of(anotherCustomer()));

        service.rename(defaultId(), "newName");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(customerWithName("newName")));

        service.delete(defaultId());
        assertThat(service.search(defaultId())).isEmpty();
        assertThat(service.search(anotherId())).isEqualTo(Optional.of(anotherCustomer()));
    }
}