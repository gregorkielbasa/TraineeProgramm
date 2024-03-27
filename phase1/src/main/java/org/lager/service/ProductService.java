package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.repository.ProductCsvEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ProductService {
    private long newProductNumber = 100_000_000;
    private final Map<Long, Product> products;
    private final ProductCsvEditor csvEditor;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductCsvEditor csvEditor) {
        this.csvEditor = csvEditor;
        this.products = new HashMap<>();
        loadFromFile();
    }

    public List<Product> getAll() {
        return new ArrayList<>(products.values());
    }

    public Product insert(String newProductName) {
        logger.debug("ProductService starts to insert new Product with {} ID and {} name", newProductNumber, newProductName);
        Product newProduct = new Product(newProductNumber, newProductName);
        products.put(newProductNumber, newProduct);
        saveToFile();
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
        saveToFile();
    }

    public void rename(long productNumber, String productNewName) {
        logger.debug("ProductService tries to rename {} Product to {}", productNumber, productNewName);
        Product product = search(productNumber)
                .orElseThrow(() -> new NoSuchProductException(productNumber));
        product.setName(productNewName);
        saveToFile();
    }

    private void saveToFile() {
        try {
            csvEditor.saveToFile(products.values().stream().toList());
            logger.info("ProductService saved its state to CSV File");
        } catch (IOException e) {
            logger.error("ProductService was not able to save CSV File");
        }
    }

    private void loadFromFile() {
        try {
            products.clear();
            csvEditor.loadFromFile().forEach(customer -> products.put(customer.getNumber(), customer));
            setNewCustomerNumber();
            logger.info("ProductService loaded its state from CSV File");
        } catch (IOException e) {
            logger.warn("ProductService was not able to load CSV File");
        }
    }

    private void setNewCustomerNumber() {
        newProductNumber = products.keySet().stream().max(Long::compareTo).orElse(newProductNumber - 1);
        newProductNumber++;
    }
}