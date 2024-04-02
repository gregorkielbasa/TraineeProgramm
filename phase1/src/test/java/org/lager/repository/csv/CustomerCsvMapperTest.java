package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.lager.CustomerFixtures.defaultCustomer;
import static org.lager.CustomerFixtures.defaultCustomerAsCsvRecord;

@DisplayName("Customer CSV Mapper")
class CustomerCsvMapperTest implements WithAssertions {

    CustomerCsvMapper csvMapper;
    @BeforeEach
    void init() {
        csvMapper = new CustomerCsvMapper();
    }

    @Nested
    @DisplayName("when maps Customer to CSV Record")
    class customerToCsvRecord {

        @Test
        @DisplayName("and Customer is null then returns empty")
        void customerNull() {
            assertThat(csvMapper.customerToCsvRecord(null))
                    .isEmpty();
        }

        @Test
        @DisplayName("and Customer is correct")
        void properCustomer() {
            assertThat(csvMapper.customerToCsvRecord(defaultCustomer()))
                    .isEqualTo(Optional.of(defaultCustomerAsCsvRecord()));
        }
    }

    @Nested
    @DisplayName("when maps CSV Record to Customer")
    class csvRecordToCustomer {

        @Test
        @DisplayName("and Record is correct")
        void properRecord() {
            assertThat(csvMapper.csvRecordToCustomer(defaultCustomerAsCsvRecord()))
                    .isEqualTo(Optional.of(defaultCustomer()));
        }

        @Test
        @DisplayName("and CSV Record is NULL")
        void recordNull() {
            assertThat(csvMapper.csvRecordToCustomer(null))
                    .isEmpty();
        }

        @Test
        @DisplayName("and Customer CSV Record contains incorrect Customer Number")
        void incorrectNumber() {
            assertThat(csvMapper.csvRecordToCustomer("123123abc,Name"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Customer CSV Record is incomplete")
        void incompleteRecord() {
            assertThat(csvMapper.csvRecordToCustomer("123123123"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Customer CSV Record has illegal number")
        void illegalNumber() {
            assertThat(csvMapper.csvRecordToCustomer("123,Name"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Customer CSV Record has illegal name")
        void illegalName() {
            assertThat(csvMapper.csvRecordToCustomer("123123123,Name . Wrong!"))
                    .isEmpty();
        }
    }
}