package org.lager.repository.csv;

import org.lager.exception.RepositoryException;
import org.lager.model.Customer;
import org.lager.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.module.ResolutionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lager.repository.csv.CustomerCsvMapper.*;

public class CustomerCsvRepository implements CustomerRepository {
    private final String filePath;
    private final String fileHeader;

    private long newCustomerNumber = 100_000_000;
    private final Map<Long, Customer> customers;
    private final Logger logger = LoggerFactory.getLogger(CustomerCsvMapper.class);

    public CustomerCsvRepository(String filePath, String header) {
        this.filePath = filePath;
        this.fileHeader = header;
        customers = new HashMap<>();
        try {
            loadCustomersFromFile();
            logger.info("Customer Repository has loaded CSV File from path: " + filePath);
        } catch (IOException e) {
            logger.error("Customer Repository was not able to load CSV File from path: " + filePath);
        }
    }

    @Override
    public void create(Long number, Customer customer) throws RepositoryException {
        validateCustomer(number, customer);
        if (read(number).isPresent())
            throw new RepositoryException("Given number is already taken");
        customers.put(number, customer);
        try {
            saveCustomersToFile();
        } catch (IOException e) {
            logger.error("Customer Repository was not able to save CSV File from path: " + filePath);
            throw new RepositoryException("CustomerRepository was not able to save changes in CSV File");
        }
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
    public Optional<Customer> read(Long number) {
        if (number == null)
            throw new ResolutionException("Given number is NULL");
        return Optional.ofNullable(customers.get(number));
    }

    @Override
    public void update(Long number, Customer customer) throws RepositoryException {
        validateCustomer(number, customer);
        read(number).orElseThrow(() -> new RepositoryException("Given number doesn't exist"));
        customers.put(number, customer);
        try {
            saveCustomersToFile();
        } catch (IOException e) {
            logger.error("Customer Repository was not able to save CSV File from path: " + filePath);
            throw new RepositoryException("CustomerRepository was not able to save changes in CSV File");
        }
    }

    @Override
    public void delete(Long number) throws RepositoryException {
        if (number == null)
            throw new RepositoryException("Given number is NULL");
        customers.remove(number);
        try {
            saveCustomersToFile();
        } catch (IOException e) {
            logger.error("Customer Repository was not able to save CSV File from path: " + filePath);
            throw new RepositoryException("CustomerRepository was not able to save changes in CSV File");
        }
    }

    @Override
    public long getNextAvailableNumber() {
        return newCustomerNumber;
    }

    private void saveCustomersToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(fileHeader);
        for (Customer customer : customers.values()) {
            Optional<String> csvRecord = customerToCsvRecord(customer);
            if (csvRecord.isEmpty()) continue;
            writer.newLine();
            writer.write(csvRecord.get());
        }
        writer.close();
    }

    private void loadCustomersFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.lines()
                .skip(1)
                .map(CustomerCsvMapper::csvRecordToCustomer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(customer -> customers.put(customer.getNumber(), customer));
        reader.close();
        updateNewCustomerNumber();
    }

    private void updateNewCustomerNumber() {
        customers.keySet().stream()
                .max(Long::compareTo)
                .orElseGet(() -> newCustomerNumber--);
        newCustomerNumber++;
    }
}