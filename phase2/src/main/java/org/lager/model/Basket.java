package org.lager.model;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Table(name = "BASKETS")
@Entity
public class Basket {
    private final static Logger logger = LoggerFactory.getLogger(Basket.class);

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "BASKET_KEY")
    @SequenceGenerator(name = "BASKET_KEY", allocationSize = 1)
    private final long basketId;
    private final long customerId;
    @ElementCollection(targetClass = BasketItem.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "BASKET_ITEMS", joinColumns = @JoinColumn(name = "BASKET_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    private final Map<Long, BasketItem> items;

    private Basket() {
        this.basketId = 0;
        this.customerId = 0;
        this.items = Map.of();
    }

    public Basket(long customerId) {
        this(0, customerId, Map.of());
        logger.info("New Basket {} has been created.", customerId);
    }

    @PersistenceCreator
    public Basket(long basketId, long customerId, Map<Long, BasketItem> items) {
        this.basketId = basketId;
        this.customerId = customerId;
        this.items = new HashMap<>(items);
    }

    public long getBasketId() {
        return basketId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public Map<Long, Integer> getContent() {
        return items.values().stream()
                .collect(Collectors.toMap(BasketItem::productId, BasketItem::amount));
    }

    public void insert(long productId, int amount) {
        logger.debug("Basket {} is changing amount of {} Product about {}", this.basketId, productId, amount);
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
        logger.debug("Basket {} is removing {} Product", basketId, productId);
        items.remove(productId);
    }

    public int getAmountOf(long productId) {
        return isProductPresent(productId)
                ? items.get(productId).amount()
                : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basket basket)) return false;
        return basketId == basket.basketId && customerId == basket.customerId && Objects.equals(items, basket.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basketId, customerId, items);
    }

    @Override
    public String toString() {
        return "Basket{" +
                "basketId=" + basketId +
                ", customerId=" + customerId +
                ", items=" + items +
                '}';
    }
}