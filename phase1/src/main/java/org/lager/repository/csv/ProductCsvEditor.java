package org.lager.repository.csv;

import org.lager.exception.ProductCsvNullException;
import org.lager.model.Product;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductCsvEditor {
    private final String filePath;
    private final String fileHeader;

    public ProductCsvEditor(String filePath, String header) {
        this.filePath = filePath;
        this.fileHeader = header;
    }

    public List<Product> loadFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<Product> products = reader.lines()
                .skip(1)
                .map(Product::getFromCsvRecord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        reader.close();
        return products;
    }

    public void saveToFile(List<Product> products) throws IOException {
        if (products == null)
            throw new ProductCsvNullException();

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(fileHeader);

        for (Product product : products) {
            writer.newLine();
            writer.write(product.toCsvRecord());
        }
        writer.close();
    }
}