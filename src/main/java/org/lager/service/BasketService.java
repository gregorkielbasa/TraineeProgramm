package org.lager.service;

import org.lager.exception.NoSuchBasketException;
import org.lager.model.Basket;
import org.lager.model.dto.BasketDto;
import org.lager.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BasketService {
    private static final Logger logger = LoggerFactory.getLogger(BasketService.class);

    private final ProductService productService;
    private final CustomerService customerService;
    private final BasketRepository repository;

    public BasketService(BasketRepository repository, CustomerService customerService, ProductService productService) {
        this.repository = repository;
        this.customerService = customerService;
        this.productService = productService;
    }

    private Optional<Basket> find(long customerId) {
        return repository.findByCustomerId(customerId);
    }

    public BasketDto get(long customerId) {
        return find(customerId)
                .map(BasketDto::new)
                .orElse(new BasketDto(customerId, Set.of()));
    }

    public List<Long> getAllIds() {
        return repository.getAllIds();
    }

    public Map<Long, Integer> getContentOfBasket(long customerId) {
        return find(customerId)
                .map(Basket::getContent)
                .orElse(Collections.emptyMap());
    }

    public void dropBasket(long customerId) {
        logger.info("BasketService empties {} Basket", customerId);
        repository.deleteByCustomerId(customerId);
    }

    public BasketDto removeFromBasket(long customerId, long productId) {
        logger.debug("BasketService remove {} Product from {} Basket", productId, customerId);
        Basket basket = find(customerId)
                .orElseThrow(() -> new NoSuchBasketException(customerId));
        basket.remove(productId);
        return new BasketDto(repository.save(basket));
    }

    public BasketDto addToBasket(long customerId, long productId, int amount) {
        logger.debug("BasketService starts to add {} Product to {} Basket", productId, customerId);
        productService.validatePresence(productId);
        Basket basket = find(customerId)
                .orElseGet(() -> createBasket(customerId));
        basket.insert(productId, amount);
        logger.debug("BasketService finished to add {} Product to {} Basket", productId, customerId);
        return new BasketDto(repository.save(basket));
    }

    private Basket createBasket(long customerId) {
        customerService.validatePresence(customerId);
        Basket newBasket = new Basket(customerId);
        logger.info("BasketService created new empty Basket with ID {}", customerId);
        return newBasket;
    }
}