package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record XmlBasketItem(@JacksonXmlProperty(isAttribute = true, localName = "ProductID")
                            Long id,
                            @JacksonXmlProperty(isAttribute = true, localName = "Amount")
                            Integer amount) {
}
