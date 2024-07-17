package org.lager.controller;

import org.lager.exception.CustomerIllegalIdException;
import org.lager.exception.CustomerIllegalNameException;
import org.lager.exception.NoSuchCustomerException;
import org.lager.model.dto.CustomerDto;
import org.lager.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<Long> getAllIds() {
        return service.getAllIds();
    }

    @PostMapping("/{newCustomerName}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CustomerDto createCustomer(@PathVariable String newCustomerName) {
        try {
            return service.create(newCustomerName);
        } catch (CustomerIllegalNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (CustomerIllegalIdException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("{customerId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CustomerDto getCustomer(@PathVariable long customerId) {
        try {
            return service.get(customerId);
        } catch (NoSuchCustomerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("{customerId}/{customerNewName}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CustomerDto renameCustomer(@PathVariable long customerId, @PathVariable String customerNewName) {
        try {
            return service.rename(customerId, customerNewName);
        } catch (NoSuchCustomerException | CustomerIllegalNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("{customerId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteCustomer(@PathVariable long customerId) {
        service.delete(customerId);
    }
}
