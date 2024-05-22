package org.lager.model;

import org.lager.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("PRODUCTS")
public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    @Id
    private final long productId;
    private String productName;
    @Transient
    private final static Logger logger = LoggerFactory.getLogger(Product.class);

    public Product(String productName) {
        this(0, productName);
    }

    @PersistenceCreator
    public Product(long productId, String productName) {
        validateId(productId);
        validateName(productName);

        this.productId = productId;
        this.productName = productName;

        logger.info("New Product {} has been created. Its productName is {}", productId, productName);
    }

    private static void validateId(long productId) {
        if (productId != 0 && (productId < ID_MIN || productId > ID_MAX))
            throw new ProductIllegalIdException(productId);
    }

    private void validateName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductIllegalNameException(name);
    }

    public String getProductName() {
        return productName;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductName(String productName) {
        logger.info("Product {} with {} productName is changing its productName to {}.", this.productId, this.productName, productName);
        validateName(productName);
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + productId +
                ", productName='" + productName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId && Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productId);
    }
}