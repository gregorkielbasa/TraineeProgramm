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
    private final long defaultProductId = 100_000_000;
    private final static Logger logger = LoggerFactory.getLogger(ProductCsvMapper.class);

    private final Map<Long, Product> products;

    public ProductCsvRepository(CsvEditor csvEditor, ProductCsvMapper csvMapper) {
        this.csvEditor = csvEditor;
        this.csvMapper = csvMapper;
        products = new HashMap<>();
        loadProductsFromFile();
    }

    @Override
    public Optional<Product> read(Long id) {
        validateId(id);
        return Optional.ofNullable(products.get(id));
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given ID is NULL");
    }

    @Override
    public void save(Product product) throws RepositoryException {
        validateProduct(product);
        products.put(product.getId(), product);
        saveProductsToFile();
    }

    private void validateProduct(Product product) throws RepositoryException {
        if (product == null)
            throw new RepositoryException("Given Product is NULL");
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        products.remove(id);
        saveProductsToFile();
    }

    @Override
    public long getNextAvailableId() {
        return 1 + products.keySet().stream()
                .max(Long::compareTo)
                .orElse(defaultProductId - 1);
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
        try {
            List<String> csvRecords = csvEditor.loadFromFile();
            logger.info("Product Repository has loaded CSV File");

            csvRecords.stream()
                    .map(csvMapper::csvRecordToProduct)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(product -> products.put(product.getId(), product));
        } catch (IOException e) {
            logger.error("Product Repository was not able to load CSV File");
        }
    }
}
