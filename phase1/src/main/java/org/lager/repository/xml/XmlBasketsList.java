package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Baskets")
public record XmlBasketsList(@JacksonXmlElementWrapper(useWrapping = false)
                             @JacksonXmlProperty(localName = "Basket")
                             List<XmlBasket> baskets) {}
