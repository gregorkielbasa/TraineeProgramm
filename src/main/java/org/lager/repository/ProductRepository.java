package org.lager.repository;

import org.lager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    default List<Long> getAllIds() {
        return findAll().stream()
                .map(Product::getProductId)
                .toList();
    }
}
