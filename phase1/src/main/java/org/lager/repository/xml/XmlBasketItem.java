package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record XmlBasketItem(@JacksonXmlProperty(isAttribute = true, localName = "ProductNumber")
                            Long number,
                            @JacksonXmlProperty(isAttribute = true, localName = "Amount")
                            Integer amount) {
}
