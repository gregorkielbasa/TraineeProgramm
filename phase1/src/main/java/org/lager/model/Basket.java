package org.lager.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Basket {

    private final long customerId;
    private final Map<Long, Integer> products; //ProductId, Amount
    private final static Logger logger = LoggerFactory.getLogger(Basket.class);

    public Basket(long customerId) {
        this.customerId = customerId;
        this.products = new HashMap<>();
        logger.info("New Basket {} has been created.", customerId);
    }

    public Map<Long, Integer> getContent() {
        return Map.copyOf(products);
    }

    public void insert(long productId, int amount) {
        logger.debug("Basket {} is changing amount of {} Product about {}", customerId, productId, amount);
        int newAmount = amount;
        if (isProductPresent(productId))
            newAmount += products.get(productId);
        if (newAmount > 0)
            products.put(productId, newAmount);
        else
            products.remove(productId);
    }

    private boolean isProductPresent(long productId) {
        return products.containsKey(productId);
    }

    public void remove(long productId) {
        logger.debug("Basket {} is removing {} Product", customerId, productId);
        products.remove(productId);
    }

    public int getAmountOf(long productId) {
        return isProductPresent(productId)
                ? products.get(productId)
                : 0;
    }

    public void concatWith(Basket basket) {
        if (basket != null)
            for (Map.Entry<Long, Integer> entry : basket.getContent().entrySet())
                this.insert(entry.getKey(), entry.getValue());
    }

    public long getCustomerId() {
        return customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return customerId == basket.customerId && Objects.equals(products, basket.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(products, customerId);
    }
}