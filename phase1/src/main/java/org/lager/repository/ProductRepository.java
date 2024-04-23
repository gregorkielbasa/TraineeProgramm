package org.lager.repository;

import org.lager.model.Product;

public interface ProductRepository extends Repository<Product, Long> {

    long getNextAvailableId();
}
