package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.dto.CustomerDto;
import org.lager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.lager.CustomerFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("integrated CustomerService")
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:integrationtest.properties")
class TestCustomerServiceIntegration implements WithAssertions {

    @Autowired
    CustomerService service;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    public void cleanUp() {
        jdbcTemplate.execute("DELETE FROM CUSTOMERS;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS CUSTOMER_KEY RESTART WITH 100000000;");
    }

    @Test
    @DisplayName("creates")
    public void testCreateCustomer() {
        //When
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
        CustomerDto customer = service.create(defaultCustomerName());

        //Then
        assertThat(customer).isEqualTo(new CustomerDto(defaultCustomer()));
    }

    @Test
    @DisplayName("validates presence")
    void validPresenceTest() {
        //Given
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
        CustomerDto customer = service.create(defaultCustomerName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultCustomerId()));
        assertThatThrownBy(() -> service.validatePresence(anotherCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
    }

    @Test
    @DisplayName("deletes existing")
    void deletesExisting() {
        //Given
        CustomerDto customer = service.create(defaultCustomerName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultCustomerId()));
        service.delete(defaultCustomerId());
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
        //Then
        assertThat(customer).isEqualTo(new CustomerDto(defaultCustomer()));
    }

    @Test
    @DisplayName("deletes non existing")
    void deletesNonExisting() {
        //Given

        //When
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
        service.delete(defaultCustomerId());
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);

        //Then
    }

    @Test
    @DisplayName("renames existing")
    void renamesExisting() {
        //Given
        service.create(defaultCustomerName());

        //When
        assertThatNoException().isThrownBy(() -> service.validatePresence(defaultCustomerId()));
        service.rename(defaultCustomerId(), "newName");
        CustomerDto customer = service.get(defaultCustomerId());

        //Then
        assertThat(customer).isEqualTo(new CustomerDto(defaultCustomerWithName("newName")));
    }

    @Test
    @DisplayName("renames non existing")
    void renamesNonExisting() {
        //Given

        //When
        assertThatThrownBy(() -> service.validatePresence(defaultCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
        assertThatThrownBy(() -> service.rename(defaultCustomerId(), "newName"))
                .isInstanceOf(NoSuchCustomerException.class);

        //Then
    }
}