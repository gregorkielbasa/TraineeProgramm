package org.lager.service;

import org.lager.exception.BasketServiceException;
import org.lager.model.Basket;
import org.lager.model.Customer;
import org.lager.model.Product;

import java.util.HashMap;
import java.util.Map;

public class BasketService {

    private final ProductService productService;
    private final CustomerService customerService;
    private static final Map<Long, Basket> baskets = new HashMap<>();
    private final Basket basket;
    private long customerNumber;

    public BasketService(ProductService productService, CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;

        this.basket = new Basket();
        customerNumber = 0;
    }

    private void logCustomerIn(long customerNumber) {
        Customer customer = customerService.search(customerNumber);
        if (customer == null)
            throw new BasketServiceException("Failed to log in as: " + customerNumber);

        this.customerNumber = customerNumber;
        Basket oldBasket = baskets.get(customerNumber);
        this.basket.concatWith(oldBasket);
    }

    public Map<Product, Integer> getBasket() {
        return basket.getAll();
    }

    public void addToBasket(String productName, int amount) {
        Product newProduct = productService.search(productName);
        basket.insert(newProduct, amount);
    }

    public void removeFromBasket(String productName) {
        Product product = productService.search(productName);
        basket.remove(product);
    }
}