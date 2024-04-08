package org.lager.repository;

import org.lager.model.Order;

public interface OrderRepository extends Repository<Order, Long> {

    long getNextAvailableNumber();
}
