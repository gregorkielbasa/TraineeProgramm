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

        XmlEditor.XmlBasket basket1 = new XmlEditor.XmlBasket(123L,
                List.of(new XmlEditor.XmlProduct(111L, 1),
                        new XmlEditor.XmlProduct(222L, 3)));
        XmlEditor.XmlBasket basket2 = new XmlEditor.XmlBasket(200L,
                List.of(new XmlEditor.XmlProduct(444L, 1),
                        new XmlEditor.XmlProduct(555L, 5),
                        new XmlEditor.XmlProduct(666L, 9)));

        XmlEditor.BasketsList basketsList = new XmlEditor.BasketsList(List.of(basket1, basket2));

        xmlEditor.saveToFile(basketsList);

        xmlEditor.loadFromFile()
                .baskets()
                .forEach(System.out::println);
    }
}