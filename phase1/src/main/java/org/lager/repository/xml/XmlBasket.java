package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Map;

public class XmlBasket {
    @JacksonXmlProperty(localName = "Number")
    public Long number;

    @JacksonXmlProperty(localName = "Products")
    public Map<Long, Integer> products;

    public XmlBasket(long number, Map<Long, Integer> products) {
        this.number = number;
        this.products = products;
    }

    public XmlBasket() {
    }
}