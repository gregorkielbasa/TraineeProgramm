package org.lager.model;

import java.util.*;

public class Basket {

    private Map<Product, Integer> products = new HashMap<>();

    public Map<Product, Integer> getAll() {
        return products;
    }

    public void remove(Product product) {
        if (isProductPresent(product))
            products.remove(product);
    }

    private boolean isProductPresent(Product product) {
        if (null == product)
            throw new RuntimeException("Product is invalid");
        return products.containsKey(product);
    }

    public void update(Product product, int newAmount) {
        if (isProductPresent(product) && newAmount > 0)
            products.replace(product, newAmount);
        else
            remove(product);
    }

    public void insert(Product newProduct, int newAmount) {
        if (isProductPresent(newProduct))
            update(newProduct, newAmount);
        else if (newAmount > 0)
            products.put(newProduct, newAmount);
    }
}
