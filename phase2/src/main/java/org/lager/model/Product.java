package org.lager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.ProductIllegalPriceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Table(name = "PRODUCTS")
@Entity
public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final static Logger logger = LoggerFactory.getLogger(Product.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_KEY")
    @SequenceGenerator(name = "PRODUCT_KEY", initialValue = (int) ID_MIN, allocationSize = 1)
    private final long productId;
    @NotBlank
    private String productName;
    @NotNull
    private double productPrice;

    private Product() {
        productId = 0;
        productName = "";
        productPrice = 0.0;
    }

    public Product(String productName, double productPrice) {
        this(0, productName, productPrice);
        logger.info("New Product {} has been created.", productName);
    }

    public Product(long productId, String productName, double productPrice) {
        validateId(productId);
        validateName(productName);
        validatePrice(productPrice);

        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    private static void validateId(long productId) {
        if (productId != 0 && (productId < ID_MIN || productId > ID_MAX))
            throw new ProductIllegalIdException(productId);
    }

    private void validateName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductIllegalNameException(name);
    }

    private void validatePrice(double productPrice) {
        if (productPrice < 0)
            throw new ProductIllegalPriceException(productPrice);
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductName(String productName) {
        logger.info("Product {} with {} productName is changing its productName to {}.", this.productId, this.productName, productName);
        validateName(productName);
        this.productName = productName;
    }

    public void setProductPrice(double productPrice) {
        logger.info("Product {} with {} productName is changing its productPrice to {}.", this.productId, this.productName, productPrice);
        validatePrice(productPrice);
        this.productPrice = productPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId && Double.compare(productPrice, product.productPrice) == 0 && Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productPrice);
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }
}