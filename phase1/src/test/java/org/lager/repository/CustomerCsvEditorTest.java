package org.lager.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lager.model.Customer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer CSV Editor")
class CustomerCsvEditorTest {

    @Test
    void loadFromFile() throws IOException {
        CustomerCsvEditor csvEditor = new CustomerCsvEditor("test.csv", "header");

        Customer customer1 = new Customer(123_123_123, "properName");
        Customer customer2 = new Customer(123_456_789, "otherNme");
        Customer customer3 = new Customer(123_000_000, "bestName");

        csvEditor.saveToFile(List.of(customer1, customer2, customer3));
    }

    @Test
    void saveToFile() {
    }
}