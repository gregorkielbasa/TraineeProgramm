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

    private long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;
    private final Logger logger = LoggerFactory.getLogger(CustomerCsvMapper.class);

    public CustomerCsvRepository(CsvEditor csvEditor, CustomerCsvMapper csvMapper) {
        this.csvEditor = csvEditor;
        this.csvMapper = csvMapper;
        customers = new HashMap<>();
        loadCustomersFromFile();
        updateNewCustomerNumber();
    }

    private void updateNewCustomerNumber() {
        newCustomerNumber = customers.keySet().stream()
                .max(Long::compareTo)
                .orElseGet(() -> --newCustomerNumber);
        newCustomerNumber++;
    }

    @Override
    public Optional<Customer> read(Long number) {
        validateNumber(number);
        return Optional.ofNullable(customers.get(number));
    }

    private void validateNumber(Long number) throws RepositoryException {
        if (number == null)
            throw new RepositoryException("Given Number is NULL");
    }

    @Override
    public void save(Customer customer) throws RepositoryException {
        validateCustomer(customer);
        customers.put(customer.getNumber(), customer);
        saveCustomersToFile();
        updateNewCustomerNumber();
    }

    private void validateCustomer(Customer customer) throws RepositoryException {
        if (customer == null)
            throw new RepositoryException("Given Customer is NULL");
    }

    @Override
    public void delete(Long number) throws RepositoryException {
        validateNumber(number);
        customers.remove(number);
        saveCustomersToFile();
        updateNewCustomerNumber();
    }

    @Override
    public long getNextAvailableNumber() {
        return newCustomerNumber;
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
        List<String> csvRecords = new ArrayList<>();

        try {
            csvRecords = csvEditor.loadFromFile();
            logger.info("Customer Repository has loaded CSV File");
        } catch (IOException e) {
            logger.error("Customer Repository was not able to load CSV File");
        }

        csvRecords.stream()
                .map(csvMapper::csvRecordToCustomer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(customer -> customers.put(customer.getNumber(), customer));
    }
}