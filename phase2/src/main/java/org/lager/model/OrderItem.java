package org.lager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ITEMS")
public record OrderItem(@Id long productId, int amount) {
    public OrderItem() {
        this(0, 0);
    }
}
