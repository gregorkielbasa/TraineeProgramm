package org.lager.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Basket {

    private final Map<Long, Integer> products; //ProductNumber, Amount
    private final long customerNumber;
    private final Logger logger = LoggerFactory.getLogger(Basket.class);

    public Basket(long customerNumber) {
        this.products = new HashMap<>();
        this.customerNumber = customerNumber;
        logger.info("New Basket {} has been created.", customerNumber);
    }

    public Map<Long, Integer> getContent() {
        return Map.copyOf(products);
    }

    public void insert(long productId, int amount) {
        logger.debug("Basket {} is changing amount of {} Product about {}", customerNumber, productId, amount);
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
        logger.debug("Basket {} is removing {} Product", customerNumber, productId);
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

    public long getCustomerNumber() {
        return customerNumber;
    }
}