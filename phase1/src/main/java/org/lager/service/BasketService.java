package org.lager.service;

import org.lager.exception.BasketServiceException;
import org.lager.model.Basket;

import java.util.HashMap;
import java.util.Map;

public class BasketService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final Map<Long, Basket> baskets;

    public BasketService(CustomerService customerService, ProductService productService) {
        this.customerService = customerService;
        this.productService = productService;
        this.baskets = new HashMap<>();
    }

    public void emptyBasket(long customerNumber) {
        baskets.remove(customerNumber);
    }

    public void removeFromBasket(long customerNumber, long productID) {
        Basket basket = baskets.get(customerNumber);
        if (basket != null)
            basket.remove(productID);
    }

    public Map<Long, Integer> getBasket(long customerNumber) {
        Basket basket = baskets.get(customerNumber);
        if (basket == null)
            return new HashMap<>();
        return basket.getAll();
    }

    public void addToBasket(long customerNumber, long productID, int amount) {
        Basket basket = baskets.get(customerNumber);
        if (basket == null)
            basket = createBasket(customerNumber);
        validateProduct(productID);
        basket.insert(productID, amount);
    }

    private Basket createBasket(long customerNumber) {
        validateCustomer(customerNumber);
        Basket newBasket = new Basket(customerNumber);
        baskets.put(customerNumber, newBasket);
        return newBasket;
    }

    private void validateCustomer(long customerNumber) {
        if (customerService.search(customerNumber) == null)
            throw new BasketServiceException("Customer does not exist: " + customerNumber);
    }

    private void validateProduct(long productID) {
        if (productService.search(productID) == null)
            throw new BasketServiceException("Product does not exist: " + productID);
    }
}