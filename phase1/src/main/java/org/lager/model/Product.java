package org.lager.model;

import java.util.Objects;

public class Product {
    private String name;

    public Product(String name) {
        if (isNameUnacceptable(name))
            throw new ProductException("Product's name is unacceptable");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isNameUnacceptable(String name) {
        return (null == name || name.isEmpty());
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