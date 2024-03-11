package org.lager.model;

import org.lager.exception.BasketException;
import org.lager.service.Catalogue;

import java.util.*;

public class Basket {

    private Map<Product, Integer> products;
    private Catalogue catalogue;

    public Basket(Catalogue catalogue) {
        this.products = new HashMap<>();
        this.catalogue = catalogue;
    }

    public Map<Product, Integer> getAll() {
        return products;
    }

    public void insert(Product newProduct) {
        if (!isProductInCatalogue(newProduct))
            throw new BasketException("Product does not exist in the Catalogue");
        if (isProductPresent(newProduct))
            throw new BasketException("Product is already in a Basket");
        products.put(newProduct, 1);
    }

    private boolean isProductInCatalogue(Product product) {
        if (null == product)
            throw new RuntimeException("Product is invalid");
        return catalogue.search(product.getName()) != null;
    }

    private boolean isProductPresent(Product product) {
        if (null == product)
            throw new RuntimeException("Product is invalid");
        return products.containsKey(product);
    }

    public void remove(Product product) {
        if (!isProductPresent(product))
            throw new BasketException("Product is not in a Basket");

        products.remove(product);
    }

    public void increase(Product product) {
        if (!isProductPresent(product))
            throw new BasketException("Product is not in a Basket");

        int oldAmount = products.get(product);
        int newAmount = oldAmount++;
        products.put(product, newAmount);
    }

    public void decrease(Product product) {
        if (!isProductPresent(product))
            throw new BasketException("Product is not in a Basket");

        int oldAmount = products.get(product);
        int newAmount = oldAmount--;

        if (newAmount > 0)
            products.put(product, newAmount);
        else
            products.remove(product);
    }

}

//    public void remove(Product product) {
//        if (isProductPresent(product))
//            products.remove(product);
//    }
//
//
//    public void update(Product product, int newAmount) {
//        if (isProductPresent(product) && newAmount > 0)
//            products.replace(product, newAmount);
//        else
//            remove(product);
//    }
//
//    public void insert(Product newProduct, int newAmount) {
//        if (isProductPresent(newProduct))
//            update(newProduct, newAmount);
//        else if (newAmount > 0)
//            products.put(newProduct, newAmount);
//    }
//}
