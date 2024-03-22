package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductService {
    private long newProductNumber = 100_000_000;
    private final Map<Long, Product> products;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService() {
        this.products = new HashMap<>();
    }

    public List<Product> getAll() {
        return new ArrayList<>(products.values());
    }

    public Product insert(String newProductName) {
        logger.debug("ProductService starts to insert new Product with {} ID and {} name", newProductNumber, newProductName);
        Product newProduct = new Product(newProductNumber, newProductName);
        products.put(newProductNumber, newProduct);
        logger.debug("ProductService finished to insert new {} Product", newProductNumber);
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

    public void remove(long productNumber) {
        logger.info("ProductService removes {} Product", productNumber);
        products.remove(productNumber);
    }

    public void rename(long productNumber, String productNewName) {
        logger.debug("ProductService tries to rename {} Product to {}", productNumber, productNewName);
        Product product = search(productNumber)
                .orElseThrow(() -> new NoSuchProductException(productNumber));
        product.setName(productNewName);
    }
}