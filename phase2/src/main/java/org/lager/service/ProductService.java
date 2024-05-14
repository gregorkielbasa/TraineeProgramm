package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(String newProductName) {
        long newProductId = repository.getNextAvailableId();
        logger.debug("ProductService starts to insert new Product with {} ID and {} name", newProductId, newProductName);
        Product newProduct = new Product(newProductId, newProductName);
        repository.save(newProduct);
        logger.debug("ProductService finished to insert new {} Product", newProductId);
        return newProduct;
    }

    public Optional<Product> search(long productId) {
        return repository.read(productId);
    }

    public void validatePresence(long productId) {
        search(productId)
                .orElseThrow(() -> new NoSuchProductException(productId));
    }

    public void delete(long productId) {
        logger.info("ProductService deletes {} Product", productId);
        repository.delete(productId);
    }

    public void rename(long productId, String productNewName) {
        logger.debug("ProductService tries to rename {} Product to {}", productId, productNewName);
        Product product = search(productId)
                .orElseThrow(() -> new NoSuchProductException(productId));
        product.setName(productNewName);
        repository.save(product);
    }
}