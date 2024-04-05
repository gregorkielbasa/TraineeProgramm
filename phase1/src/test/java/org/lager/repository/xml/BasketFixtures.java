package org.lager.repository.xml;

import org.lager.model.Basket;

import java.util.ArrayList;
import java.util.List;

public class BasketFixtures {
    private final static long CUSTOMER_1_NUMBER = 100_000_000L;
    private final static long CUSTOMER_2_NUMBER = 100_000_001L;
    private final static long PRODUCT_1_NUMBER = 100_000_000L;
    private final static long PRODUCT_2_NUMBER = 100_000_001L;

    public static Basket defaultEmptyBasket() {

        return new Basket(CUSTOMER_1_NUMBER);
    }

    public static Basket defaultBasket() {

        Basket basket = new Basket(CUSTOMER_1_NUMBER);
        basket.insert(PRODUCT_1_NUMBER, 1);
        return basket;
    }

    public static Basket anotherBasket() {

        Basket basket = new Basket(CUSTOMER_2_NUMBER);
        basket.insert(PRODUCT_1_NUMBER, 2);
        basket.insert(PRODUCT_2_NUMBER, 3);
        return basket;
    }

    public static XmlBasket defaultXmlEmptyBasket() {

        return new XmlBasket(CUSTOMER_1_NUMBER, List.of());
    }

    public static XmlBasket defaultXmlBasket() {

        List<XmlBasketItem> items = new ArrayList<>();
        items.add(new XmlBasketItem(PRODUCT_1_NUMBER, 1));

        return new XmlBasket(CUSTOMER_1_NUMBER, items);
    }

    public static XmlBasket anotherXmlBasket() {

        List<XmlBasketItem> items = new ArrayList<>();
        items.add(new XmlBasketItem(PRODUCT_1_NUMBER, 2));
        items.add(new XmlBasketItem(PRODUCT_2_NUMBER, 3));

        return new XmlBasket(CUSTOMER_2_NUMBER, items);
    }
}
