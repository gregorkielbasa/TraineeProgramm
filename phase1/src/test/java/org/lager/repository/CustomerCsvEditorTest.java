package org.lager.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lager.exception.CustomerCsvNullException;
import org.lager.model.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Customer CSV Editor")
class CustomerCsvEditorTest implements WithAssertions {

    @Test
    @DisplayName("throws an exception when loads non existing CSV file")
    void loadNonExisting() {
        CustomerCsvEditor csvEditor = new CustomerCsvEditor("nonExistingFile.csv", "header");

        assertThatThrownBy(csvEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("throws an exception when saves NULL List")
    void saveNull() {
        CustomerCsvEditor csvEditor = new CustomerCsvEditor("any.csv", "header");

        assertThatThrownBy(() -> csvEditor.saveToFile(null))
                .isInstanceOf(CustomerCsvNullException.class);
    }

    @Nested
    @DisplayName("writes and loads")
    class CustomerCsvWorks {
        private final Customer CUSTOMER_1 = new Customer(123_123_123, "properName");
        private final Customer CUSTOMER_2 = new Customer(123_456_789, "otherName");
        private CustomerCsvEditor csvEditor;
        private List<Customer> customers;

        @BeforeEach
        void init() {
            csvEditor = new CustomerCsvEditor("test.csv", "number,name");
            customers = new ArrayList<>();
        }

        @Test
        @DisplayName("empty list")
        void emptyList() throws IOException {
            csvEditor.saveToFile(customers);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of());
        }

        @Test
        @DisplayName("list with one Customer")
        void listWithOneCustomer() throws IOException {
            customers.add(CUSTOMER_1);

            csvEditor.saveToFile(customers);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1));
        }

        @Test
        @DisplayName("list with two Customer")
        void listWithTwoCustomer() throws IOException {
            customers.add(CUSTOMER_1);
            customers.add(CUSTOMER_2);

            csvEditor.saveToFile(customers);
            assertThat(csvEditor.loadFromFile()).containsExactlyInAnyOrderElementsOf(List.of(CUSTOMER_1, CUSTOMER_2));
        }
    }
}