package org.lager.service;

import org.lager.exception.NoSuchBasketException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class BasketService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final BasketRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(BasketService.class);

    public BasketService(BasketRepository repository, CustomerService customerService, ProductService productService) {
        this.repository = repository;
        this.customerService = customerService;
        this.productService = productService;
    }

    private Optional<Basket> getBasket(long customerNumber) {
        return repository.read(customerNumber);
    }

    public Map<Long, Integer> getContentOfBasket(long customerNumber) {
        return getBasket(customerNumber)
                .map(Basket::getContent)
                .orElse(Collections.emptyMap());
    }

    public void emptyBasket(long customerNumber) {
        logger.info("BasketService empties {} Basket", customerNumber);
        repository.delete(customerNumber);
    }

    public void removeFromBasket(long customerNumber, long productNumber) {
        logger.debug("BasketService remove {} Product from {} Basket", productNumber, customerNumber);
        Basket basket = getBasket(customerNumber)
                .orElseThrow(() -> new NoSuchBasketException(customerNumber));
        basket.remove(productNumber);
        repository.save(basket);
    }

    public void addToBasket(long customerNumber, long productNumber, int amount) {
        logger.debug("BasketService starts to add {} Product to {} Basket", productNumber, customerNumber);
        productService.validatePresence(productNumber);
        Basket basket = getBasket(customerNumber)
                .orElseGet(() -> createBasket(customerNumber));
        basket.insert(productNumber, amount);
        repository.save(basket);
        logger.debug("BasketService finished to add {} Product to {} Basket", productNumber, customerNumber);
    }

    private Basket createBasket(long customerNumber) {
        customerService.validatePresence(customerNumber);
        Basket newBasket = new Basket(customerNumber);
        logger.info("BasketService created new Basket with ID {}", customerNumber);
        return newBasket;
    }
}