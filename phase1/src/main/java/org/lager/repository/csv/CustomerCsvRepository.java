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

    private long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;
    private final Logger logger = LoggerFactory.getLogger(CustomerCsvMapper.class);

    public CustomerCsvRepository(CsvEditor csvEditor) {
        this.csvEditor = csvEditor;
        customers = new HashMap<>();
        loadCustomersFromFile();
        updateNewCustomerNumber();
    }

    private void updateNewCustomerNumber() {
        customers.keySet().stream()
                .max(Long::compareTo)
                .orElseGet(() -> newCustomerNumber - 1);
        newCustomerNumber++;
    }

    @Override
    public Optional<Customer> read(Long number) {
        if (number == null)
            return Optional.empty();
        return Optional.ofNullable(customers.get(number));
    }

    @Override
    public void create(Long number, Customer customer) throws RepositoryException {
        validateCustomer(number, customer);
        if (read(number).isPresent())
            throw new RepositoryException("Given number is already taken");
        customers.put(number, customer);
        updateNewCustomerNumber();
        saveCustomersToFile();
    }

    private static void validateCustomer(Long number, Customer customer) throws RepositoryException {
        if (number == null)
            throw new RepositoryException("Given number is NULL");
        if (customer == null)
            throw new RepositoryException("Given Customer is NULL");
        if (number != customer.getNumber())
            throw new RepositoryException("Given Customer doesn't match given Number");
    }

    @Override
    public void update(Long number, Customer customer) throws RepositoryException {
        validateCustomer(number, customer);
        if (read(number).isEmpty())
            throw new RepositoryException("Given number doesn't exist");
        customers.put(number, customer);
        saveCustomersToFile();
    }

    @Override
    public void delete(Long number) throws RepositoryException {
        if (number == null)
            throw new RepositoryException("Given number is NULL");
        customers.remove(number);
        updateNewCustomerNumber();
        saveCustomersToFile();
    }

    @Override
    public long getNextAvailableNumber() {
        return newCustomerNumber;
    }

    private void saveCustomersToFile() {
        List<String> csvRecords = customers.values().stream()
                .map(CustomerCsvMapper::customerToCsvRecord)
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
                .map(CustomerCsvMapper::csvRecordToCustomer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(customer -> customers.put(customer.getNumber(), customer));
    }
}