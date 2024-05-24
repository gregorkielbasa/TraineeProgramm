package org.lager.repository;

import org.lager.model.Basket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends CrudRepository<Basket, Long> {

    Optional<Basket> findByCustomerId(long customerId);

    void deleteByCustomerId(long customerId);
}
