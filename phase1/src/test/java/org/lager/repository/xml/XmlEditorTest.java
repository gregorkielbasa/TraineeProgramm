package org.lager.repository.xml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlEditorTest {

    XmlEditor xmlEditor;
    @Test
    @DisplayName("cos")
    void first() throws IOException {
        xmlEditor = new XmlEditor("xmlEditorTest.xml");

        Map<Long, Integer> products = new HashMap<>();
        products.put(223L, 1);
        products.put(200L, 5);
        products.put(220L, 5);
        products.put(250L, 5);

        XmlBasket basket1 = new XmlBasket(123_123_123, products);

        products.put(300L, 9);
        XmlBasket basket2 = new XmlBasket(123_000_000, products);

        xmlEditor.saveToFile(List.of(basket1, basket2));

        xmlEditor.loadFromFile()
                .forEach(System.out::println);
    }
}