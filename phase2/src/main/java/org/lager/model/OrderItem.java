package org.lager.model;

import org.springframework.data.relational.core.mapping.Table;

@Table("ORDER_ITEMS")
public record OrderItem(long productId, int amount) {
}
