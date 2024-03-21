package org.lager.service;

import org.lager.model.Basket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasketService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final Map<Long, Basket> baskets;
    private final Logger logger = LoggerFactory.getLogger(BasketService.class);

    public BasketService(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
        this.baskets = new HashMap<>();
    }

    private Optional<Basket> getBasket(long customerNumber) {
        return Optional.ofNullable(baskets.get(customerNumber));
    }

    public void emptyBasket(long customerNumber) {
        logger.info("BasketService empties {} Basket", customerNumber);
        baskets.remove(customerNumber);
    }

    public void removeFromBasket(long customerNumber, long productNumber) {
        logger.debug("BasketService remove {} Product from {} Basket", productNumber, customerNumber);
        getBasket(customerNumber)
                .ifPresent((basket) -> basket.remove(productNumber));
    }

    public Map<Long, Integer> getContentOfBasket(long customerNumber) {
        return getBasket(customerNumber)
                .map(Basket::getContent)
                .orElse(Collections.emptyMap());
    }

    public void addToBasket(long customerNumber, long productNumber, int amount) {
        logger.debug("BasketService starts to add {} Product to {} Basket", productNumber, customerNumber);
        productService.validatePresence(productNumber);
        Basket basket = getBasket(customerNumber)
                .orElseGet(() -> createBasket(customerNumber));
        basket.insert(productNumber, amount);
        logger.debug("BasketService finished to add {} Product to {} Basket", productNumber, customerNumber);
    }

    private Basket createBasket(long customerNumber) {
        customerService.validatePresence(customerNumber);
        Basket newBasket = new Basket(customerNumber);
        baskets.put(customerNumber, newBasket);
        logger.info("BasketService created new Basket with ID {}", customerNumber);
        return newBasket;
    }
}