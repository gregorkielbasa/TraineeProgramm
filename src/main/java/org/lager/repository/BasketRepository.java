package org.lager.repository;

import org.lager.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByCustomerId(long customerId);

    default List<Long> getAllIds() {
        return findAll().stream()
                .map(Basket::getCustomerId)
                .toList();
    }

    default void deleteByCustomerId(long customerId){
        Optional<Basket> basket = findByCustomerId(customerId);
        basket.ifPresent(this::delete);
    }
}
