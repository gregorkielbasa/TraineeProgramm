package org.lager.repository;

import org.lager.model.Customer;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerCsvEditor {
    private final String filePath;
    private final String fileHeader;

    public CustomerCsvEditor(String filePath, String header) {
        this.filePath = filePath;
        this.fileHeader = header;
    }

    public List<Customer> loadFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<Customer> customers = reader.lines()
                .skip(1)
                .map(Customer::getFromCsvRecord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        reader.close();
        return customers;
    }

    public void saveToFile(List<Customer> customers) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(fileHeader);

        for (Customer customer : customers) {
            writer.newLine();
            writer.write(customer.toCsvRecord());
        }
        writer.close();
    }
}