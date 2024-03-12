package org.lager.model;

import org.lager.exception.BasketException;

import java.util.*;

public class Basket {

    private final Map<Product, Integer> products;
    private long customerNumber = 0;

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
            throw new BasketException("Product does not exist");
        return products.containsKey(product);
    }

    public void remove(Product product) {
        if (null == product)
            throw new BasketException("Product does not exist");
        products.remove(product);
    }

    public int getAmountOf(Product product) {
        if (null == product)
            throw new BasketException("Product does not exist");
        if (isProductPresent(product))
            return products.get(product);
        else
            return 0;
    }

    public void concatWith(Basket basket) {
        if (basket != null)
            for (Map.Entry<Product, Integer> entry : basket.getAll().entrySet()) {
                this.insert(entry.getKey(), entry.getValue());
            }
    }

    public long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(long customerNumber) {
        this.customerNumber = customerNumber;
    }
}