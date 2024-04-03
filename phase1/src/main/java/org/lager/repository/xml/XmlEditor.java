package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlEditor {
    private final String filePath;
    private final XmlMapper xmlMapper;

    public XmlEditor(String filePath) {
        this.filePath = filePath;
        this.xmlMapper = new XmlMapper();
    }

    @JacksonXmlRootElement(localName = "Baskets")
    private static class BasketList {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Basket")
        public List<XmlBasket> baskets;

        public BasketList(List<XmlBasket> baskets) {
            this.baskets = baskets;
        }

        public BasketList() {
            baskets = new ArrayList<>();
        }
    }

    public void saveToFile(List<XmlBasket> records) throws IOException {
        xmlMapper.writeValue(new File(filePath), new BasketList(records));
    }

    public List<XmlBasket> loadFromFile() throws IOException {
        return xmlMapper.readValue(new File(filePath), BasketList.class).baskets;
    }
}

