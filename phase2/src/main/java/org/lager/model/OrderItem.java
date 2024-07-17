package org.lager.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record OrderItem(long productId, int amount) {
    private OrderItem() {
        this(0, 0);
    }
}
