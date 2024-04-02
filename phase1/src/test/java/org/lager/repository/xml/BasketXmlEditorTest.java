package org.lager.repository.xml;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.lager.exception.BasketXmlNullException;
import org.lager.model.Basket;
import org.lager.repository.xml.BasketXmlEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Basket XML Editor")
class BasketXmlEditorTest implements WithAssertions {

    @Test
    @DisplayName("throws an exception when loads non existing CSV file")
    void loadNonExisting() {
        BasketXmlEditor xmlEditor = new BasketXmlEditor("nonExistingFile.xml");

        assertThatThrownBy(xmlEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("")
    void saveNull() {
        BasketXmlEditor xmlEditor = new BasketXmlEditor("any.xml");

        assertThatThrownBy(() -> xmlEditor.saveToFile(null))
                .isInstanceOf(BasketXmlNullException.class);
    }

    @Nested
    @DisplayName("writes and loads")
    class BasketXmlWorks {
        private static final long CUSTOMER_NUMBER_1 = 123_123_123L;
        private static final long CUSTOMER_NUMBER_2 = 123_456_789L;
        private final static Basket BASKET_1 = new Basket(CUSTOMER_NUMBER_1);
        private final static Basket BASKET_2 = new Basket(CUSTOMER_NUMBER_2);
        private final BasketXmlEditor xmlEditor = new BasketXmlEditor("Baskets.xml");
        private List<Basket> baskets;

        @BeforeAll
        static void init() {
            BASKET_1.insert(100_100_100, 1);
            BASKET_1.insert(200_200_200, 2);
            BASKET_2.insert(100_100_100, 11);
            BASKET_2.insert(220_220_220, 20);
        }

        @BeforeEach
        void init2() {
            baskets = new ArrayList<>();
        }

        @Test
        @DisplayName("list with one Baskets")
        void emptyList() throws IOException {
            xmlEditor.saveToFile(baskets);
        }

        @Test
        @DisplayName("list with one Baskets")
        void listWithOneBaskets() throws IOException {
            baskets.add(BASKET_1);

            xmlEditor.saveToFile(baskets);
        }

        @Test
        @DisplayName("list with two Baskets")
        void listWithTwoBaskets() throws IOException {
            baskets.add(BASKET_1);
            baskets.add(BASKET_2);

            xmlEditor.saveToFile(baskets);
        }
    }
}