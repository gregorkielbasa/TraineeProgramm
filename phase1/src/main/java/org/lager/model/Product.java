package org.lager.model;

import org.lager.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long NUMBER_MIN = 100_000_000;
    private static final long NUMBER_MAX = 999_999_999;
    private final long number;
    private String name;
    private final static Logger logger = LoggerFactory.getLogger(Product.class);

    public Product(long number, String name) {
        validateNumber(number);
        validateName(name);

        this.number = number;
        this.name = name;

        logger.info("New Product {} has been created. Its name is {}", number, name);
    }

    private static void validateNumber(long number) {
        if (number < NUMBER_MIN || number > NUMBER_MAX)
            throw new ProductIllegalNumberException(number);
    }

    private void validateName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductIllegalNameException(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("Product {} with {} name is changing its name to {}.", this.number, this.name, name);
        validateName(name);
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Product{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return number == product.number && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
}