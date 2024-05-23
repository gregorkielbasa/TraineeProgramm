package org.lager.repository;

import org.lager.model.Basket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends CrudRepository<Basket, Long> {
}
