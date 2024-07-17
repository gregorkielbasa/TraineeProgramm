package org.lager.model.dto;

import org.lager.model.Basket;
import org.lager.model.BasketItem;

import java.util.Set;

public record BasketDto(long customerId, Set<BasketItem> items) {

    public BasketDto(Basket basket) {
        this(basket.getCustomerId(), basket.getItems());
    }
}
