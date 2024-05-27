package org.lager;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.Customer;
import org.lager.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.lager.CustomerFixtures.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CustomerService")
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
        jdbcTemplate.execute("DELETE FROM CUSTOMERS;");  // Adjust the SQL as per your database
        jdbcTemplate.execute("ALTER TABLE CUSTOMERS ALTER COLUMN CUSTOMER_ID RESTART WITH 100000000;");  // Adjust the SQL as per your database
    }

    @Test
    @DisplayName("creats")
    public void testCreateCustomer() {
        //When
        Optional<Customer> customerBefore = service.search(defaultCustomerId());
        Customer customer = service.create(defaultCustomerName());
        Optional<Customer> customerAfter = service.search(defaultCustomerId());

        //Then
        assertThat(customerBefore).isEmpty();
        assertThat(customer).isEqualTo(defaultCustomer());
        assertThat(customerAfter).isEqualTo(Optional.of(defaultCustomer()));
    }

    @Test
    @DisplayName("validates presence")
    void validPresenceTest() {
        //Given
        Customer customer = service.create(defaultCustomerName());

        //When
        service.validatePresence(defaultCustomerId());
        assertThatThrownBy(() -> service.validatePresence(anotherCustomerId()))
                .isInstanceOf(NoSuchCustomerException.class);
    }

    @Test
    @DisplayName("deletes existing")
    void deletesExisting() {
        //Given
        Customer customer = service.create(defaultCustomerName());

        //When
        Optional<Customer> customerBefore = service.search(defaultCustomerId());
        service.delete(defaultCustomerId());
        Optional<Customer> customerAfter = service.search(defaultCustomerId());

        //Then
        assertThat(customerBefore).isEqualTo(Optional.of(defaultCustomer()));
        assertThat(customer).isEqualTo(defaultCustomer());
        assertThat(customerAfter).isEmpty();
    }

    @Test
    @DisplayName("deletes non existing")
    void deletesNonExisting() {
        //Given

        //When
        Optional<Customer> customerBefore = service.search(defaultCustomerId());
        service.delete(defaultCustomerId());
        Optional<Customer> customerAfter = service.search(defaultCustomerId());

        //Then
        assertThat(customerBefore).isEmpty();
        assertThat(customerAfter).isEmpty();
    }

    @Test
    @DisplayName("renames existing")
    void renamesExisting() {
        //Given
        Customer customer = service.create(defaultCustomerName());

        //When
        Optional<Customer> customerBefore = service.search(defaultCustomerId());
        service.rename(defaultCustomerId(), "newName");
        Optional<Customer> customerAfter = service.search(defaultCustomerId());

        //Then
        assertThat(customerBefore).isEqualTo(Optional.of(defaultCustomer()));
        assertThat(customer).isEqualTo(defaultCustomer());
        assertThat(customerAfter).isEqualTo(Optional.of(defaultCustomerWithName("newName")));
    }

    @Test
    @DisplayName("renames non existing")
    void renamesNonExisting() {
        //Given

        //When
        Optional<Customer> customerBefore = service.search(defaultCustomerId());
        assertThatThrownBy(() -> service.rename(defaultCustomerId(), "newName"))
                .isInstanceOf(NoSuchCustomerException.class);

        //Then
        assertThat(customerBefore).isEmpty();
    }
}