package org.lager.service;

import org.lager.model.Product;

import java.util.HashMap;
import java.util.Map;

public class Catalogue {

    private Map<String, Product> database;

    public Catalogue() {
        this.database = new HashMap();
    }

    public void insert (Product newProduct){
        String key = newProduct.getName();
        database.put(key, newProduct);
    }
    public Product search (String name){
        return database.get(name);
    }

    public void remove (String name){
        database.remove(name);
    }
}