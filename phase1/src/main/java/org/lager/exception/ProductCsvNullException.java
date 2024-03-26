package org.lager.exception;

public class ProductCsvNullException extends RuntimeException {
    public ProductCsvNullException() {
        super("Product CSV Editor cannot save NULL List");
    }
}
