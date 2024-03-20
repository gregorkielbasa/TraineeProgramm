package org.lager.service;

import org.lager.model.Basket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasketService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final Map<Long, Basket> baskets;

    public BasketService(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
        this.baskets = new HashMap<>();
    }

    private Optional<Basket> getBasket(long customerNumber) {
        return Optional.ofNullable(baskets.get(customerNumber));
    }

    public void emptyBasket(long customerNumber) {
        baskets.remove(customerNumber);
    }

    public void removeFromBasket(long customerNumber, long productNumber) {
        getBasket(customerNumber)
                .ifPresent((basket) -> basket.remove(productNumber));
    }

    public Map<Long, Integer> getContentOfBasket(long customerNumber) {
        return getBasket(customerNumber)
                .map(Basket::getContent)
                .orElse(Collections.emptyMap());
    }

    public void addToBasket(long customerNumber, long productNumber, int amount) {
        productService.validatePresence(productNumber);
        Basket basket = getBasket(customerNumber)
                .orElseGet(() -> createBasket(customerNumber));
        basket.insert(productNumber, amount);
    }

    private Basket createBasket(long customerNumber) {
        customerService.validatePresence(customerNumber);
        Basket newBasket = new Basket(customerNumber);
        baskets.put(customerNumber, newBasket);
        return newBasket;
    }
}