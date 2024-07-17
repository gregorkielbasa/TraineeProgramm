package org.lager.model.dto;

import org.lager.model.Product;

public record ProductDto (long productId, String productName) {

    public ProductDto(Product product) {
        this(product.getProductId(), product.getProductName());
    }
}