package org.lager.controller;

import org.lager.exception.*;
import org.lager.model.dto.BasketDto;
import org.lager.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService service;

    public BasketController(BasketService service) {
        this.service = service;
    }

    @GetMapping("/")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Long> getAllIds() {
        return service.getAllIds();
    }

    @GetMapping("/{customerId}")
    @ResponseStatus(code = HttpStatus.OK)
    public BasketDto getBasket(@PathVariable long customerId) {
        try {
            return service.get(customerId);
        } catch (NoSuchBasketException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteBasket(@PathVariable long customerId) {
        service.dropBasket(customerId);
    }

    @DeleteMapping("/{customerId}/{productId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public BasketDto removeFromBasket(@PathVariable long customerId, @PathVariable long productId) {
        try {
            return service.removeFromBasket(customerId, productId);
        } catch (NoSuchProductException | NoSuchCustomerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping({"/{customerId}/{productId}", "/{customerId}/{productId}/{amount}"})
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public BasketDto addToBasket(@PathVariable long customerId, @PathVariable long productId, @PathVariable(value = "1") int amount) {
        try {
            return service.addToBasket(customerId, productId, amount);
        } catch (NoSuchProductException | NoSuchCustomerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
