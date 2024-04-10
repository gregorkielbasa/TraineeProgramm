package org.lager.repository.xml;

import org.lager.model.Basket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class BasketXmlMapper {

    private final Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public BasketXmlMapper() {
    }

    public List<Basket> xmlToBasketsList(XmlBasketsList xmlBaskets) {
        if (xmlBaskets == null || xmlBaskets.baskets() == null)
            return List.of();

        return xmlBaskets.baskets().stream()
                .map(this::xmlToBasket)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<Basket> xmlToBasket(XmlBasket xmlBasket) {
        if (xmlBasket == null || xmlBasket.customerNumber() == null || xmlBasket.items() == null || xmlBasket.items().isEmpty())
            return Optional.empty();

        Basket result = new Basket(xmlBasket.customerNumber());

        xmlBasket.items().stream()
                .filter(validateXmlBasketItem())
                .forEach(item -> result.insert(item.number(), item.amount()));

        return result.getContent().isEmpty()
                ? Optional.empty()
                : Optional.of(result);
    }

    private Predicate<XmlBasketItem> validateXmlBasketItem() {
        return basketItem -> basketItem != null && basketItem.number() != null && basketItem.amount() != null;
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
        if (basket.getContent().isEmpty())
            return Optional.empty();

        List<XmlBasketItem> result = basket.getContent().entrySet().stream()
                .map(basketItemToXml())
                .sorted(Comparator.comparingLong(XmlBasketItem::number))                                                //is it really needed?
                .toList();

        return Optional.of(new XmlBasket(basket.getCustomerNumber(), result));
    }

    private Function<Map.Entry<Long, Integer>, XmlBasketItem> basketItemToXml() {
        return entry -> new XmlBasketItem(entry.getKey(), entry.getValue());
    }
}
