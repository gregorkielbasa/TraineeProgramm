package org.lager.service;

import org.lager.exception.ProductServiceException;
import org.lager.model.Product;

import java.util.*;

public class ProductService {

    private final Map<String, Product> products;

    public ProductService() {
        this.products = new HashMap<>();
    }

    public List<Product> getAll() {
        return new ArrayList<>(products.values());
    }

    public Product insert(String newProductName) {
        Product newProduct = new Product(newProductName);
        String key = newProduct.getName();
        if (!isProductNew(key))
            throw new ProductServiceException("Product already exists in the Catalogue: " + newProductName);
        return products.put(key, newProduct);
    }

    private boolean isProductNew(String name) {
        return search(name) == null;
    }

    public Product search(String name) {
        if (null == name)
            return null;
        return products.get(name);
    }

    public Product remove(String name) {
        if (null != name)
            return products.remove(name);
        return null;
    }
}