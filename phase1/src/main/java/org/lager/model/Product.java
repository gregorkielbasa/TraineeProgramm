package org.lager.model;

import org.lager.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final long id;
    private String name;
    private final static Logger logger = LoggerFactory.getLogger(Product.class);

    public Product(long id, String name) {
        validateId(id);
        validateName(name);

        this.id = id;
        this.name = name;

        logger.info("New Product {} has been created. Its name is {}", id, name);
    }

    private static void validateId(long id) {
        if (id < ID_MIN || id > ID_MAX)
            throw new ProductIllegalIdException(id);
    }

    private void validateName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductIllegalNameException(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("Product {} with {} name is changing its name to {}.", this.id, this.name, name);
        validateName(name);
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}