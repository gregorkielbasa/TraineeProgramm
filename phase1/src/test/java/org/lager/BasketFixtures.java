package org.lager;

import org.lager.model.Basket;
import org.lager.repository.xml.XmlBasket;
import org.lager.repository.xml.XmlBasketItem;
import org.lager.repository.xml.XmlBasketsList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasketFixtures {
    private final static long CUSTOMER_1_ID = 100_000_000L;
    private final static long CUSTOMER_2_ID = 100_000_001L;
    private final static long PRODUCT_1_ID = 100_000_000L;
    private final static long PRODUCT_2_ID = 100_000_001L;

    public static long defaultCustomerId() {
        return CUSTOMER_1_ID;
    }

    public static long anotherCustomerId() {
        return CUSTOMER_2_ID;
    }

    public static long defaultProductId() {
        return PRODUCT_1_ID;
    }

    public static Basket defaultEmptyBasket() {

        return new Basket(CUSTOMER_1_ID);
    }

    public static Basket defaultBasket() {

        Basket basket = new Basket(CUSTOMER_1_ID);
        basket.insert(PRODUCT_1_ID, 1);
        return basket;
    }

    public static Basket defaultBasketWith(long product, int amount) {

        Basket basket = new Basket(CUSTOMER_1_ID);
        basket.insert(product, amount);
        return basket;
    }

    public static Basket anotherBasket() {

        Basket basket = new Basket(CUSTOMER_2_ID);
        basket.insert(PRODUCT_1_ID, 2);
        basket.insert(PRODUCT_2_ID, 3);
        return basket;
    }

    public static Map<Long, Integer> basketContentOf(Basket basket) {
        return basket.getContent();
    }

    public static XmlBasket defaultXmlEmptyBasket() {

        return new XmlBasket(CUSTOMER_1_ID, List.of());
    }

    public static XmlBasket defaultXmlBasket() {

        List<XmlBasketItem> items = new ArrayList<>();
        items.add(new XmlBasketItem(PRODUCT_1_ID, 1));

        return new XmlBasket(CUSTOMER_1_ID, items);
    }

    public static XmlBasket anotherXmlBasket() {

        List<XmlBasketItem> items = new ArrayList<>();
        items.add(new XmlBasketItem(PRODUCT_1_ID, 2));
        items.add(new XmlBasketItem(PRODUCT_2_ID, 3));

        return new XmlBasket(CUSTOMER_2_ID, items);
    }

    public static XmlBasketsList defaultXmlList() {
        return new XmlBasketsList(List.of(defaultXmlBasket()));
    }

    public static XmlBasketsList anotherXmlList() {
        return new XmlBasketsList(List.of(anotherXmlBasket(), defaultXmlBasket()));
    }
}
