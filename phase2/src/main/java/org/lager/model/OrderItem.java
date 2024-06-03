package org.lager.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record OrderItem(long productId, int amount) {
    public OrderItem() {
        this(0, 0);
    }
}
