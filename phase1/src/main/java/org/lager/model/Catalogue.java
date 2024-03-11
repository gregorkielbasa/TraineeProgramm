package org.lager.model;

import org.lager.exception.CatalogueException;
import org.lager.model.Product;

import java.util.*;

public class Catalogue {

    private final Map<String, Product> products;

    public Catalogue() {
        this.products = new HashMap<>();
    }

    public List<Product> insert(Product newProduct) {
        if (null == newProduct)
            throw new CatalogueException("Product is not valid");
        String key = newProduct.getName();
        if (!isProductNew(key))
            throw new CatalogueException("Product already exists in the Catalogue");

        products.put(key, newProduct);
        return new ArrayList<>(products.values());
    }

    private boolean isProductNew(String name) {
        return search(name) == null;
    }

    public Product search(String name) {
        if (null == name)
            return null;
        return products.get(name);
    }

    public List<Product> remove(String name) {
        if (null != name)
            products.remove(name);
        return new ArrayList<>(products.values());
    }
}