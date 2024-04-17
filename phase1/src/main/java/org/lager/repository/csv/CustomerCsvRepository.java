package org.lager.repository.csv;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CustomerCsvRepository implements CustomerRepository {
    private final CsvEditor csvEditor;
    private final CustomerCsvMapper csvMapper;
    private final long defaultCustomerId = 100_000_000;
    private final static Logger logger = LoggerFactory.getLogger(CustomerCsvMapper.class);

    private final Map<Long, Customer> customers;

    public CustomerCsvRepository(CsvEditor csvEditor, CustomerCsvMapper csvMapper) {
        this.csvEditor = csvEditor;
        this.csvMapper = csvMapper;
        customers = new HashMap<>();
        loadCustomersFromFile();
    }

    @Override
    public Optional<Customer> read(Long id) {
        validateId(id);
        return Optional.ofNullable(customers.get(id));
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given ID is NULL");
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);
        customers.put(customer.getId(), customer);
        saveCustomersToFile();
    }

    private void validateCustomer(Customer customer) throws RepositoryException {
        if (customer == null)
            throw new RepositoryException("Given Customer is NULL");
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        customers.remove(id);
        saveCustomersToFile();
    }

    @Override
    public long getNextAvailableId() {
        return customers.keySet().stream()
                .max(Long::compareTo)
                .map(index -> index + 1)
                .orElse(defaultCustomerId);
    }

    private void saveCustomersToFile() {
        List<String> csvRecords = customers.values().stream()
                .map(csvMapper::customerToCsvRecord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        try {
            csvEditor.saveToFile(csvRecords);
        } catch (IOException e) {
            logger.error("Customer Repository was not able to save CSV File");
            throw new RepositoryException("CustomerRepository was not able to save changes in CSV File");
        }
    }

    private void loadCustomersFromFile() {
        try {
            List<String> csvRecords = csvEditor.loadFromFile();
            logger.info("Customer Repository has loaded CSV File");

            csvRecords.stream()
                    .map(csvMapper::csvRecordToCustomer)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(customer -> customers.put(customer.getId(), customer));
        } catch (IOException e) {
            logger.error("Customer Repository was not able to load CSV File");
        }
    }
}