package org.lager.service;

import org.lager.exception.NoSuchProductException;
import org.lager.model.Product;
import org.lager.model.dto.ProductDto;
import org.lager.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final static Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductDto create(String newProductName) {
        logger.debug("ProductService starts to insert new Product with {} name", newProductName);
        Product newProduct = repository.save(new Product(newProductName, 0.0));
        logger.debug("ProductService finished to insert new {} Product", newProduct.getProductId());
        return new ProductDto(newProduct);
    }

    private Optional<Product> find(long productId) {
        return repository.findById(productId);
    }

    public ProductDto get(long productId) {
        return find(productId)
                .map(ProductDto::new)
                .orElseThrow(() -> new NoSuchProductException(productId));
    }

    public List<Long> getAllIds() {
        return repository.getAllIds();
    }

    public void validatePresence(long productId) {
        find(productId)
                .orElseThrow(() -> new NoSuchProductException(productId));
    }

    public void delete(long productId) {
        logger.info("ProductService deletes {} Product", productId);
        repository.deleteById(productId);
    }

    public ProductDto rename(long productId, String productNewName) {
        logger.debug("ProductService tries to rename {} Product to {}", productId, productNewName);
        Product product = find(productId)
                .orElseThrow(() -> new NoSuchProductException(productId));
        product.setProductName(productNewName);
        return new ProductDto(repository.save(product));
    }
}