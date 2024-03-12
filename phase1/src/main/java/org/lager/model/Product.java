package org.lager.model;

import org.lager.exception.ProductException;

import java.util.Objects;

public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private String name;

    public Product(String name) {
        validName(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void validName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductException("Product's name is invalid: " + name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}