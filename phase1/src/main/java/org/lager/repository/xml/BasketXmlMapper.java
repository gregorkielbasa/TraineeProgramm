package org.lager.repository.xml;

import org.lager.model.Basket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class BasketXmlMapper {

    private final Logger logger = LoggerFactory.getLogger(BasketXmlMapper.class);

    public BasketXmlMapper() {
    }

    public Optional<Basket> xmlRecordToBasket (XmlBasket xmlRecord) {
        Optional<Basket> result = Optional.empty();
        try {
            long number = xmlRecord.number;
            Map<Long, Integer> products = xmlRecord.products;
            Basket newBasket = new Basket(number);
            for (Map.Entry<Long, Integer> entry : products.entrySet())
                newBasket.insert(entry.getKey(), entry.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<XmlBasket> basketToXmlRecord (Basket basket) {
        if (basket == null) {
            logger.warn("Basket is NULL");
            return Optional.empty();
        }

        XmlBasket result = new XmlBasket(basket.getCustomerNumber(), basket.getContent());
        return Optional.of(result);
    }
}
