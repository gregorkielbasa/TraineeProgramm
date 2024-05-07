package org.lager.repository.csv;

import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CustomerCsvMapper {
    private final static Logger logger = LoggerFactory.getLogger(CustomerCsvMapper.class);

    public Optional<Customer> csvRecordToCustomer(String csvRecord) {
        Optional<Customer> result = Optional.empty();
        try {
            String[] values = csvRecord.split(",");
            long id = Long.parseLong(values[0]);
            String name = values[1];
            Customer newCustomer = new Customer(id, name);
            result = Optional.of(newCustomer);
        } catch (NullPointerException e) {
            logger.warn("Customer CSV Record is NULL");
        } catch (NumberFormatException e) {
            logger.warn("Customer CSV Record contains incorrect Customer's ID");
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warn("Customer CSV Record is incomplete");
        } catch (CustomerIllegalIdException | CustomerIllegalNameException e) {
            logger.warn("Customer CSV Record is invalid: {}", e.getMessage());
        }
        return result;
    }

    public Optional<String> customerToCsvRecord(Customer customer) {
        if (customer == null) {
            logger.warn("Customer is NULL");
            return Optional.empty();
        }

        String result = customer.getId() + "," + customer.getName();
        return Optional.of(result);
    }
}
