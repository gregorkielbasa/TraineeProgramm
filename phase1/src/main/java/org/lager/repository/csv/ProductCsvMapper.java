package org.lager.repository.csv;

import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.ProductIllegalNumberException;
import org.lager.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ProductCsvMapper {
    private final Logger logger = LoggerFactory.getLogger(ProductCsvMapper.class);

    public Optional<Product> csvRecordToProduct(String csvRecord) {
        Optional<Product> result = Optional.empty();
        try {
            String[] values = csvRecord.split(",");
            long number = Long.parseLong(values[0]);
            String name = values[1];
            Product newProduct = new Product(number, name);
            result = Optional.of(newProduct);
        } catch (NullPointerException e) {
            logger.warn("Product CSV Record is NULL");
        } catch (NumberFormatException e) {
            logger.warn("Product CSV Record contains incorrect Product Number");
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warn("Product CSV Record is incomplete");
        } catch (ProductIllegalNumberException | ProductIllegalNameException e) {
            logger.warn("Product CSV Record is invalid: " + e);
        }
        return result;
    }

    public Optional<String> productToCsvRecord(Product product) {
        if (product == null) {
            logger.warn("Product is NULL");
            return Optional.empty();
        }

        String result = product.getNumber() + "," + product.getName();
        return Optional.of(result);
    }
}
