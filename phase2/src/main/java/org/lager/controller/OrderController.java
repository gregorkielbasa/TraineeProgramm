package org.lager.controller;

import org.lager.exception.*;
import org.lager.model.dto.OrderDto;
import org.lager.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<Long> getAllIds() {
        return service.getAllIds();
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(code = HttpStatus.OK)
    public OrderDto getOrder(@PathVariable long orderId) {
        try {
            return service.get(orderId);
        } catch (NoSuchOrderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{customerId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public OrderDto makeOrder(@PathVariable long customerId) {
        try {
            return service.order(customerId);
        } catch (OrderIllegalIdException | OrderItemSetNotPresentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
