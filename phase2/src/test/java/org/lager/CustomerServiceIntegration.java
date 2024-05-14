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
class CustomerServiceIntegration implements WithAssertions {

    @Autowired
    CustomerService service;

    @Test
    void properCase() {

        assertThat(service.search(defaultId())).isEmpty();
        service.create("customerOne");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(defaultCustomer()));

        service.rename(defaultId(), "newName");
        assertThat(service.search(defaultId())).isEqualTo(Optional.of(customerWithName("newName")));

        service.delete(defaultId());
        assertThat(service.search(defaultId())).isEmpty();
    }
}