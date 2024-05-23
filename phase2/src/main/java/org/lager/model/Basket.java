package org.lager.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import javax.swing.text.html.parser.Entity;
import java.util.*;
import java.util.stream.Collectors;

@Table("BASKETS")
public class Basket {

    @Id
    private final long customerId;
    @MappedCollection(idColumn = "CUSTOMER_ID", keyColumn = "PRODUCT_ID")
    private final Map<Long, BasketItem> items;
    private final static Logger logger = LoggerFactory.getLogger(Basket.class);

    public Basket(long customerId) {
        this.customerId = customerId;
        this.items = new HashMap<>();
        logger.info("New Basket {} has been created.", customerId);
    }

    public Map<Long, Integer> getContent() {
        return items.values().stream()
                .collect(Collectors.toMap(BasketItem::productId, BasketItem::amount));
    }

    public void insert(long productId, int amount) {
        logger.debug("Basket {} is changing amount of {} Product about {}", customerId, productId, amount);
        int newAmount = amount;
        if (isProductPresent(productId))
            newAmount += items.get(productId).amount();
        if (newAmount > 0)
            items.put(productId, new BasketItem(productId, newAmount));
        else
            items.remove(productId);
    }

    private boolean isProductPresent(long productId) {
        return items.containsKey(productId);
    }

    public void remove(long productId) {
        logger.debug("Basket {} is removing {} Product", customerId, productId);
        items.remove(productId);
    }

    public int getAmountOf(long productId) {
        return isProductPresent(productId)
                ? items.get(productId).amount()
                : 0;
    }

    public long getCustomerId() {
        return customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return customerId == basket.customerId && Objects.equals(items, basket.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, customerId);
    }
}