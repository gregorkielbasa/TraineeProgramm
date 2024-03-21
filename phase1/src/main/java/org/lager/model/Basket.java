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

    public void insert(long productID, int amount) {
        logger.debug("Basket {} is changing amount of {} Product about {}", customerNumber, productID, amount);
        int newAmount = amount;
        if (isProductPresent(productID))
            newAmount += products.get(productID);
        if (newAmount > 0)
            products.put(productID, newAmount);
        else
            products.remove(productID);
    }

    private boolean isProductPresent(long productID) {
        return products.containsKey(productID);
    }

    public void remove(long productID) {
        logger.debug("Basket {} is removing {} Product", customerNumber, productID);
        products.remove(productID);
    }

    public int getAmountOf(long productID) {
        return isProductPresent(productID)
                ? products.get(productID)
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