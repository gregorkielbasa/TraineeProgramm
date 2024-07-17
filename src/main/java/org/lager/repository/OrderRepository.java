package org.lager.repository;

import org.lager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    default List<Long> getAllIds() {
        return findAll().stream()
                .map(Order::getOrderId)
                .toList();
    }
}
