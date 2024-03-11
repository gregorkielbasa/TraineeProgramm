package org.lager.model;

import org.lager.exception.BasketException;

import java.util.*;

public class Basket {

    private final Map<Product, Integer> products;

    public Basket() {
        this.products = new HashMap<>();
    }

    public Map<Product, Integer> getAll() {
        return products;
    }

    public void insert(Product product, int amount) {
        int newAmount = amount;
        if (isProductPresent(product))
            newAmount += products.get(product);
        if (newAmount > 0)
            products.put(product, newAmount);
        else
            products.remove(product);
    }

    private boolean isProductPresent(Product product) {
        if (null == product)
            throw new BasketException("Product is invalid");
        return products.containsKey(product);
    }

    public void remove(Product product) {
        if (null == product)
            throw new BasketException("Product is invalid");
        products.remove(product);
    }

    public int getAmountOf(Product product) {
        if (null == product)
            throw new BasketException("Product is invalid");
        if (isProductPresent(product))
            return products.get(product);
        else
            return 0;
    }
}