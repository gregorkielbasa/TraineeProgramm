package org.lager;

import org.lager.model.Basket;

import java.util.Map;

import static org.lager.CustomerFixtures.anotherCustomerId;
import static org.lager.CustomerFixtures.defaultCustomerId;
import static org.lager.ProductFixtures.anotherProductId;
import static org.lager.ProductFixtures.defaultProductId;

public class BasketFixtures {
    private final static long BASKET_1_ID = 1L;
    private final static long BASKET_2_ID = 2L;
    private final static long CUSTOMER_1_ID = defaultCustomerId();
    private final static long CUSTOMER_2_ID = anotherCustomerId();
    private final static long PRODUCT_1_ID = defaultProductId();
    private final static long PRODUCT_2_ID = anotherProductId();

    public static Basket defaultEmptyBasket() {
        return new Basket(BASKET_1_ID, CUSTOMER_1_ID, Map.of());
    }

    public static Basket defaultNewBasket() {
        Basket basket = new Basket(CUSTOMER_1_ID);
        basket.insert(PRODUCT_1_ID, 1);
        return basket;
    }

    public static Basket defaultNewEmptyBasket() {
        return new Basket(CUSTOMER_1_ID);
    }

    public static Basket defaultNewBasketWith(long product, int amount) {
        Basket basket = new Basket(CUSTOMER_1_ID);
        basket.insert(product, amount);
        return basket;
    }

    public static Basket defaultBasket() {
        Basket basket = new Basket(BASKET_1_ID, CUSTOMER_1_ID, Map.of());
        basket.insert(PRODUCT_1_ID, 1);
        return basket;
    }

    public static Basket defaultBasketWith(long product, int amount) {
        Basket basket = new Basket(BASKET_1_ID, CUSTOMER_1_ID, Map.of());
        basket.insert(product, amount);
        return basket;
    }

    public static Basket anotherBasket() {
        Basket basket = new Basket(BASKET_2_ID, CUSTOMER_2_ID, Map.of());
        basket.insert(PRODUCT_1_ID, 2);
        basket.insert(PRODUCT_2_ID, 3);
        return basket;
    }

    public static Basket anotherNewBasket() {
        Basket basket = new Basket(CUSTOMER_2_ID);
        basket.insert(PRODUCT_1_ID, 2);
        basket.insert(PRODUCT_2_ID, 3);
        return basket;
    }

    public static Basket anotherBasketWith(long product, int amount) {
        Basket basket = new Basket(BASKET_2_ID, CUSTOMER_2_ID, Map.of());
        basket.insert(product, amount);
        return basket;
    }

    public static Map<Long, Integer> basketContentOf(Basket basket) {
        return basket.getContent();
    }
}
