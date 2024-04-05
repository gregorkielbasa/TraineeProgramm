package org.lager.repository.xml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class XmlEditorTest {

    XmlEditor xmlEditor;
    @Test
    @DisplayName("cos")
    void first() throws IOException {
        xmlEditor = new XmlEditor("xmlEditorTest.xml");

        XmlBasket basket1 = new XmlBasket(123_123_123L, List.of(
                new XmlBasketItem(123L, 3),
                new XmlBasketItem(234L, 4)
        ));
        XmlBasket basket2 = new XmlBasket(123_123_123L, List.of(
                new XmlBasketItem(123L, 13),
                new XmlBasketItem(234L, 17),
                new XmlBasketItem(345L, 19)
        ));

        XmlBasketsList baskets = new XmlBasketsList(List.of(basket1, basket2));

        xmlEditor.saveToFile(baskets);

        xmlEditor.loadFromFile()
                .baskets()
                .forEach(System.out::println);
    }
}