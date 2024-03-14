package org.lager.model;

import org.lager.exception.ProductException;

import java.util.Objects;

public class Product {
    private static final String NAME_REGEX = "^[a-zA-Z0-9- ]{3,24}$";
    private static final long ID_MIN = 100_000_000;
    private static final long ID_MAX = 999_999_999;
    private final long ID;
    private String name;

    public Product(long ID, String name) {
        validID(ID);
        validName(name);

        this.ID = ID;
        this.name = name;
    }

    private static void validID(long ID) {
        if (ID < ID_MIN || ID > ID_MAX)
            throw new ProductException("Product's ID is invalid: " + ID);
    }

    private void validName(String name) {
        if (null == name || !name.matches(NAME_REGEX))
            throw new ProductException("Product's name is invalid: " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validName(name);
        this.name = name;
    }

    public long getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ID == product.ID && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ID);
    }
}