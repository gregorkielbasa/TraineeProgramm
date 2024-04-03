package org.lager.repository.csv;

import org.lager.exception.RepositoryException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ProductCsvRepository implements ProductRepository {
    private final CsvEditor csvEditor;
    private final ProductCsvMapper csvMapper;

    private long newProductNumber = 100_000_000;
    private final Map<Long, Product> products;
    private final Logger logger = LoggerFactory.getLogger(ProductCsvMapper.class);

    public ProductCsvRepository(CsvEditor csvEditor, ProductCsvMapper csvMapper) {
        this.csvEditor = csvEditor;
        this.csvMapper = csvMapper;
        products = new HashMap<>();
        loadProductsFromFile();
        updateNewProductNumber();
    }

    private void updateNewProductNumber() {
        newProductNumber = products.keySet().stream()
                .max(Long::compareTo)
                .orElseGet(() -> --newProductNumber);
        newProductNumber++;
    }

    @Override
    public Optional<Product> read(Long number) {
        validateNumber(number);
        return Optional.ofNullable(products.get(number));
    }

    private void validateNumber(Long number) throws RepositoryException {
        if (number == null)
            throw new RepositoryException("Given Number is NULL");
    }

    @Override
    public void save(Product product) throws RepositoryException {
        validateProduct(product);
        products.put(product.getNumber(), product);
        saveProductsToFile();
        updateNewProductNumber();
    }

    private void validateProduct(Product product) throws RepositoryException {
        if (product == null)
            throw new RepositoryException("Given Product is NULL");
    }

    @Override
    public void delete(Long number) throws RepositoryException {
        validateNumber(number);
        products.remove(number);
        saveProductsToFile();
        updateNewProductNumber();
    }

    @Override
    public long getNextAvailableNumber() {
        return newProductNumber;
    }

    private void saveProductsToFile() {
        List<String> csvRecords = products.values().stream()
                .map(csvMapper::productToCsvRecord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        try {
            csvEditor.saveToFile(csvRecords);
        } catch (IOException e) {
            logger.error("Product Repository was not able to save CSV File");
            throw new RepositoryException("ProductRepository was not able to save changes in CSV File");
        }
    }

    private void loadProductsFromFile() {
        List<String> csvRecords = new ArrayList<>();

        try {
            csvRecords = csvEditor.loadFromFile();
            logger.info("Product Repository has loaded CSV File");
        } catch (IOException e) {
            logger.error("Product Repository was not able to load CSV File");
        }

        csvRecords.stream()
                .map(csvMapper::csvRecordToProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(product -> products.put(product.getNumber(), product));
    }
}
