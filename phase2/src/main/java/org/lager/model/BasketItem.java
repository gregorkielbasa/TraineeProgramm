package org.lager.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record BasketItem(long productId, int amount) {
    private BasketItem() {
        this(0, 0);
    }
}
