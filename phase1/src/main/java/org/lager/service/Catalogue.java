package org.lager.service;

import org.lager.model.Product;

import java.util.HashMap;
import java.util.Map;

public class Catalogue {

    private final Map<String, Product> database;

    public Catalogue() {
        this.database = new HashMap<>();
    }

    public void insert(Product newProduct) {
        String key = newProduct.getName();
        if (isProductNew(key))
            database.put(key, newProduct);
    }

    private boolean isProductNew(String name) {
        return search(name) == null;
    }

    public Product search(String name) {
        if (null == name)
            return null;
        return database.get(name);
    }

    public void remove(String name) {
        database.remove(name);
    }
}