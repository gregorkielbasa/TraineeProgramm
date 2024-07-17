package org.lager.model;

import org.springframework.data.relational.core.mapping.Table;

@Table("BASKET_ITEMS")
public record BasketItem(long productId, int amount) {
}
