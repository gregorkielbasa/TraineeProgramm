package org.lager.model;

import java.util.*;

public class Basket {

    private final Map<Long, Integer> products; //ProductID, Amount
    private final long customerNumber;

    public Basket(long customerNumber) {
        this.products = new HashMap<>();
        this.customerNumber = customerNumber;
    }

    public Map<Long, Integer> getAll() {
        return Map.copyOf(products);
    }

    public void insert(long productID, int amount) {
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
        products.remove(productID);
    }

    public int getAmountOf(long productID) {
        return isProductPresent(productID)
                ? products.get(productID)
                : 0;
    }

    public void concatWith(Basket basket) {
        if (basket != null)
            for (Map.Entry<Long, Integer> entry : basket.getAll().entrySet())
                this.insert(entry.getKey(), entry.getValue());
    }

    public long getCustomerNumber() {
        return customerNumber;
    }
}