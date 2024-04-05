package org.lager.repository.xml;

import org.lager.model.Basket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

public class BasketXmlMapper {

    private final Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public BasketXmlMapper() {
    }

    public List<Basket> xmlToBasketsList(XmlBasketsList xmlBaskets) {
        if (xmlBaskets == null || xmlBaskets.baskets() == null) {
            return List.of();
        }

        return xmlBaskets.baskets().stream()
                .map(this::xmlToBasket)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<Basket> xmlToBasket(XmlBasket xmlBasket) {
        if (xmlBasket == null || xmlBasket.customerNumber() == null || xmlBasket.items() == null)
            return Optional.empty();

        Basket result = new Basket(xmlBasket.customerNumber());

        xmlBasket.items().stream()
                .map(this::xmlItemToOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(item -> result.insert(item.number(), item.amount()));
        return Optional.of(result);
    }

    private Optional<XmlBasketItem> xmlItemToOptional(XmlBasketItem xmlProduct) {
        if (xmlProduct == null || xmlProduct.number() == null || xmlProduct.amount() == null)
            return Optional.empty();

        return Optional.of(xmlProduct);
    }

    public XmlBasketsList basketsListToXml(List<Basket> baskets) {
        if (baskets == null)
            return new XmlBasketsList(List.of());

        List<XmlBasket> result = baskets.stream()
                .map(this::basketToXml)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return new XmlBasketsList(result);
    }

    private Optional<XmlBasket> basketToXml(Basket basket) {
        if (basket == null || basket.getContent() == null)
            return Optional.empty();

        List<XmlBasketItem> result = basket.getContent().entrySet().stream()
                .map(x -> basketItemToXml(x.getKey(), x.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return Optional.of(new XmlBasket(basket.getCustomerNumber(), result));
    }

    private Optional<XmlBasketItem> basketItemToXml (Long number, Integer amount) {
        if (number == null || amount == null)
            return Optional.empty();

        XmlBasketItem result = new XmlBasketItem(number, amount);
        return Optional.of(result);
    }
}
