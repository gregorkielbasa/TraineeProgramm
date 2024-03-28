package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.CustomerCsvNullException;
import org.lager.model.Customer;

import java.io.IOException;

@DisplayName("Customer CSV Repository")
class CustomerCsvRepositoryTest implements WithAssertions {

    @Test
    @DisplayName("throws an exception when loads non existing CSV file")
    void loadNonExisting() {
        assertThatThrownBy(() -> new CustomerCsvRepository("nonExistingFile.csv", "header"))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("throws an exception when saves NULL List")
    void saveNull() {
        CustomerCsvRepository csvEditor = new CustomerCsvRepository("any.csv", "header");

        assertThatThrownBy(() -> csvEditor.create(null, null))
                .isInstanceOf(CustomerCsvNullException.class);
    }

    @Nested
    @DisplayName("writes and loads")
    class CustomerCsvWorks {
        private final long CUSTOMER_NUMBER_1 = 123_123_123;
        private final Customer CUSTOMER_1 = new Customer(CUSTOMER_NUMBER_1, "properName");
        private final long CUSTOMER_NUMBER_2 = 123_456_789;
        private final Customer CUSTOMER_2 = new Customer(CUSTOMER_NUMBER_2, "otherName");
        private CustomerCsvRepository repository;

        @BeforeEach
        void init() {
            repository = new CustomerCsvRepository("test.csv", "number,name");
        }

        @Test
        @DisplayName("empty list")
        void emptyList() throws IOException {
            repository.create(CUSTOMER_NUMBER_1, CUSTOMER_1);

            assertThat(repository.read(CUSTOMER_NUMBER_1))
                    .isPresent();
        }

//        @Test
//        @DisplayName("list with one Customer")
//        void listWithOneCustomer() throws IOException {
//            customers.add(CUSTOMER_1);
//
//            repository.saveToFile(customers);
//            assertThat(repository.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1));
//        }
//
//        @Test
//        @DisplayName("list with two Customer")
//        void listWithTwoCustomer() throws IOException {
//            customers.add(CUSTOMER_1);
//            customers.add(CUSTOMER_2);
//
//            repository.saveToFile(customers);
//            assertThat(repository.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
//        }
    }
}