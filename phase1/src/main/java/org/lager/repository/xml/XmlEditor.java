package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlEditor {
    private final String filePath;
    private final XmlMapper xmlMapper;

    public XmlEditor(String filePath) {
        this.filePath = filePath;
        this.xmlMapper = new XmlMapper();
    }

    public record BasketsList(@JacksonXmlElementWrapper(useWrapping = false)
                              @JacksonXmlProperty(localName = "Basket")
                              List<XmlBasket> baskets) {
    }

    public record XmlBasket(@JacksonXmlProperty(localName = "Number")
                         Long number,
                         @JacksonXmlElementWrapper(useWrapping = false)
                         @JacksonXmlProperty(localName = "Product")
                         List<XmlProduct> products) {
    }

    public record XmlProduct(@JacksonXmlProperty(isAttribute = true, localName = "Number")
                          Long number,
                             @JacksonXmlProperty(isAttribute = true, localName = "Amount")
                          Integer amount) {
    }


    public void saveToFile(BasketsList baskets) throws IOException {
        xmlMapper.writeValue(new File(filePath), baskets);
    }

    public BasketsList loadFromFile() throws IOException {
        return xmlMapper.readValue(new File(filePath), BasketsList.class);
    }
}

