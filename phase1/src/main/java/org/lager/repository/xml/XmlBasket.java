package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record XmlBasket(@JacksonXmlProperty(localName = "ID")
                        Long customerId,
                        @JacksonXmlElementWrapper(useWrapping = false)
                        @JacksonXmlProperty(localName = "Product")
                        List<XmlBasketItem> items) {
}
