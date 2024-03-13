package org.lager.service;

import org.lager.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private static long newProductID = 100_000_000;
    private final Map<Long, Product> products;

    public ProductService() {
        this.products = new HashMap<>();
    }

    public List<Product> getAll() {
        return new ArrayList<>(products.values());
    }

    public Product insert(String newProductName) {
        Product newProduct = new Product(newProductID, newProductName);
        products.put(newProductID, newProduct);
        newProductID++;
        return newProduct;
    }

    public Product search(long ID) {
        return products.get(ID);
    }

    public Product remove(long ID) {
        return products.remove(ID);
    }

    public Product rename(long ID, String ProductNewName) {
        Product product = search(ID);
        if (null != product)
            product.setName(ProductNewName);
        return product;
    }
}