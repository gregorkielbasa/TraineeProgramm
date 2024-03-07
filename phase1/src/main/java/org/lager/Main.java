package org.lager;

import org.lager.model.Product;
import org.lager.service.Catalogue;

public class Main {
    public static void main(String[] args) {

        Catalogue catalogue = new Catalogue();

        catalogue.insert(new Product("erste"));

        System.out.println(catalogue.search("erste"));
        System.out.println(catalogue.search("zweite"));
        System.out.println("-------------------------");

        catalogue.insert(new Product("zweite"));
        System.out.println(catalogue.search("zweite"));
        catalogue.remove("zweite");
        System.out.println(catalogue.search("zweite"));
    }
}
