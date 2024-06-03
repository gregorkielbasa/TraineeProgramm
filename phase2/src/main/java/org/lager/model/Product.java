package org.lager.model;

import jakarta.persistence.*;
import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;

@Table(name = "PRODUCTS")
@Entity
public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final static Logger logger = LoggerFactory.getLogger(Product.class);

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PRODUCT_KEY")
    @SequenceGenerator(name = "PRODUCT_KEY", initialValue = (int) ID_MIN, allocationSize = 1)
    private long productId;
    private String productName;

    public Product() {
    }

    public Product(String productName) {
        this(0, productName);
        logger.info("New Product {} has been created.", productName);
    }

    @PersistenceCreator
    public Product(long productId, String productName) {
        validateId(productId);
        validateName(productName);

        this.productId = productId;
        this.productName = productName;
    }

    private static void validateId(long productId) {
        if (productId != 0 && (productId < ID_MIN || productId > ID_MAX))
            throw new ProductIllegalIdException(productId);
    }

    private void validateName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductIllegalNameException(name);
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        logger.info("Product {} with {} productName is changing its productName to {}.", this.productId, this.productName, productName);
        validateName(productName);
        this.productName = productName;
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

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + productId +
                ", productName='" + productName + '\'' +
                '}';
    }
}