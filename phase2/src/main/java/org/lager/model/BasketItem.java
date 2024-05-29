package org.lager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BASKET_ITEMS")
public record BasketItem(@Id long productId, int amount) {
    public BasketItem() {
        this(0, 0);
    }
}
