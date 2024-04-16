package org.lager.model;

import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.CustomerIllegalNumberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class  Customer {
    private static final String NAME_REGEX = "^[a-zA-Z]{3,16}$";
    private static final long NUMBER_MIN = 100_000_000;
    private static final long NUMBER_MAX = 999_999_999;
    private final long number;
    private String name;

    private final static Logger logger = LoggerFactory.getLogger(Customer.class);

    public Customer(long number, String name) {
        validateNumber(number);
        validateName(name);

        this.name = name;
        this.number = number;

        logger.info("New Customer {} has been created.", number);
    }

    private static void validateNumber(long number) {
        if (number < NUMBER_MIN || number > NUMBER_MAX)
            throw new CustomerIllegalNumberException(number);
    }

    private static void validateName(String name) {
        if (name == null || !name.matches(NAME_REGEX))
            throw new CustomerIllegalNameException(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("Customer {} with {} name is changing its name to {}.", this.number, this.name, name);
        validateName(name);
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    public String toCsvRecord() {
        return number + "," + name;
    }

    public static Optional<Customer> getFromCsvRecord(String csvRecord) {
        Optional<Customer> result = Optional.empty();
        try {
            String[] values = csvRecord.split(",");
            long number = Long.parseLong(values[0]);
            String name = values[1];
            Customer newCustomer = new Customer(number, name);
            result = Optional.of(newCustomer);
        } catch (NullPointerException e) {
            logger.warn("Customer CSV Record is NULL");
        } catch (NumberFormatException e) {
            logger.warn("Customer CSV Record contains incorrect Customer Number");
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warn("Customer CSV Record is incomplete");
        } catch (CustomerIllegalNumberException | CustomerIllegalNameException e) {
            logger.warn("Customer CSV Record is invalid: " + e);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return number == customer.number && Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
}