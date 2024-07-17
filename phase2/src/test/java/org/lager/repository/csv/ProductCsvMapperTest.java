package org.lager.repository.csv;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.lager.ProductFixtures.*;

@DisplayName("Product CSV ObjectMapper")
class ProductCsvMapperTest implements WithAssertions {

    ProductCsvMapper csvMapper;
    @BeforeEach
    void init() {
        csvMapper = new ProductCsvMapper();
    }

    @Nested
    @DisplayName("when maps Product to CSV Record")
    class productToCsvRecord {

        @Test
        @DisplayName("and Product is null")
        void productNull() {
            assertThat(csvMapper.productToCsvRecord(null))
                    .isEmpty();
        }

        @Test
        @DisplayName("and Product is correct")
        void properProduct() {
            assertThat(csvMapper.productToCsvRecord(defaultProduct()))
                    .isEqualTo(Optional.of(defaultProductAsCsvRecord()));
        }
    }

    @Nested
    @DisplayName("when maps CSV Record to Product")
    class csvRecordToProduct {

        @Test
        @DisplayName("and Record is correct")
        void properRecord() {
            assertThat(csvMapper.csvRecordToProduct(defaultProductAsCsvRecord()))
                    .isEqualTo(Optional.of(defaultProduct()));
        }

        @Test
        @DisplayName("and CSV Record is NULL")
        void recordNull() {
            assertThat(csvMapper.csvRecordToProduct(null))
                    .isEmpty();
        }

        @Test
        @DisplayName("and Product CSV Record contains incorrect Product ID")
        void incorrectId() {
            assertThat(csvMapper.csvRecordToProduct("123123abc,Name"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Product CSV Record is incomplete")
        void incompleteRecord() {
            assertThat(csvMapper.csvRecordToProduct("123123123"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Product CSV Record has illegal ID")
        void illegalId() {
            assertThat(csvMapper.csvRecordToProduct("123,Name"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Product CSV Record has illegal name")
        void illegalName() {
            assertThat(csvMapper.csvRecordToProduct("123123123,Name . Wrong!"))
                    .isEmpty();
        }
    }
}