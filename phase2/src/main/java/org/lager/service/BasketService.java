package org.lager.service;

import org.lager.exception.NoSuchBasketException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
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

    private Optional<Basket> getBasket(long customerId) {
        return repository.findById(customerId);
    }

    public Map<Long, Integer> getContentOfBasket(long customerId) {
        return getBasket(customerId)
                .map(Basket::getContent)
                .orElse(Collections.emptyMap());
    }

    public void dropBasket(long customerId) {
        logger.info("BasketService empties {} Basket", customerId);
        repository.deleteById(customerId);
    }

    public void removeFromBasket(long customerId, long productId) {
        logger.debug("BasketService remove {} Product from {} Basket", productId, customerId);
        Basket basket = getBasket(customerId)
                .orElseThrow(() -> new NoSuchBasketException(customerId));
        basket.remove(productId);
        repository.save(basket);
    }

    public void addToBasket(long customerId, long productId, int amount) {
        logger.debug("BasketService starts to add {} Product to {} Basket", productId, customerId);
        productService.validatePresence(productId);
        Basket basket = getBasket(customerId)
                .orElseGet(() -> createBasket(customerId));
        basket.insert(productId, amount);
        repository.save(basket);
        logger.debug("BasketService finished to add {} Product to {} Basket", productId, customerId);
    }

    private Basket createBasket(long customerId) {
        customerService.validatePresence(customerId);
        Basket newBasket = new Basket(customerId);
        logger.info("BasketService created new Basket with ID {}", customerId);
        return newBasket;
    }
}