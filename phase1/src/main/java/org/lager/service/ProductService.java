package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;

import java.util.*;

public class ProductService {
    private long newProductNumber = 100_000_000;
    private final Map<Long, Product> products;

    public ProductService() {
        this.products = new HashMap<>();
    }

    public List<Product> getAll() {
        return new ArrayList<>(products.values());
    }

    public Product insert(String newProductName) {
        Product newProduct = new Product(newProductNumber, newProductName);
        products.put(newProductNumber, newProduct);
        newProductNumber++;
        return newProduct;
    }

    public Optional<Product> search(long number) {
        return Optional.ofNullable(products.get(number));
    }

    public boolean validatePresence(long number) {
        search(number)
                .orElseThrow(() -> new NoSuchProductException(number));
        return true;
    }

    public void remove(long number) {
        products.remove(number);
    }

    public void rename(long number, String ProductNewName) {
        Product product = search(number)
                .orElseThrow(() -> new NoSuchProductException(number));
        product.setName(ProductNewName);
    }
}