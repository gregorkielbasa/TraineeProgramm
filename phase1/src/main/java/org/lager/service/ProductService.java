package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductService {
    private final ProductRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(String newProductName) {
        long newProductNumber = repository.getNextAvailableNumber();
        logger.debug("ProductService starts to insert new Product with {} ID and {} name", newProductNumber, newProductName);
        Product newProduct = new Product(newProductNumber, newProductName);
        repository.save(newProduct);
        logger.debug("ProductService finished to insert new {} Product", newProductNumber);
        return newProduct;
    }

    public Optional<Product> search(long productNumber) {
        return repository.read(productNumber);
    }

    public void validatePresence(long productNumber) {
        search(productNumber)
                .orElseThrow(() -> new NoSuchProductException(productNumber));
    }

    public void remove(long productNumber) {
        logger.info("ProductService removes {} Product", productNumber);
        repository.delete(productNumber);
    }

    public void rename(long productNumber, String productNewName) {
        logger.debug("ProductService tries to rename {} Product to {}", productNumber, productNewName);
        Product product = search(productNumber)
                .orElseThrow(() -> new NoSuchProductException(productNumber));
        product.setName(productNewName);
        repository.save(product);
    }
}