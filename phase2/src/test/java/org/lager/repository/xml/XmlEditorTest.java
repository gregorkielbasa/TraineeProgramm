package org.lager.repository.xml;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@DisplayName("XML Editor")
class XmlEditorTest implements WithAssertions {

    @Test
    @DisplayName("loads non existing XML File")
    void nonExisting() {
        XmlEditor xmlEditor = new XmlEditor("nonExistingFile.xml");

        assertThatThrownBy(xmlEditor::loadFromFile)
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("writes and loads empty BasketsList")
    void emptyBasketsList() throws IOException {
        XmlEditor xmlEditor = new XmlEditor("xmlEditorTest.xml");

        xmlEditor.saveToFile(new XmlBasketsList(List.of()));
        assertThat(xmlEditor.loadFromFile().baskets()).isNull();
    }

    @Test
    @DisplayName("writes and loads non-empty List")
    void nonEmptyList() throws IOException {
        XmlEditor xmlEditor = new XmlEditor("xmlEditorTest.xml");

        XmlBasket basket1 = new XmlBasket(123_123_123L, List.of(
                new XmlBasketItem(123L, 3)
        ));
        XmlBasket basket2 = new XmlBasket(123_456_789L, List.of(
                new XmlBasketItem(234L, 17),
                new XmlBasketItem(123L, 13)
        ));

        XmlBasketsList baskets = new XmlBasketsList(List.of(basket1, basket2));

        xmlEditor.saveToFile(baskets);

        assertThat(xmlEditor.loadFromFile()).isEqualTo(baskets);
    }
}