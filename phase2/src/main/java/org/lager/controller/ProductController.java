package org.lager.controller;

import org.lager.exception.NoSuchProductException;
import org.lager.exception.ProductIllegalIdException;
import org.lager.exception.ProductIllegalNameException;
import org.lager.exception.ProductIllegalPriceException;
import org.lager.model.dto.ProductDto;
import org.lager.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<Long> getAllIds() {
        return service.getAllIds();
    }

    @PostMapping("/{newProductName}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ProductDto createProduct(@PathVariable String newProductName) {
        try {
            return service.create(newProductName);
        } catch (ProductIllegalNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ProductIllegalIdException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        } catch (ProductIllegalPriceException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{productId}")
    @ResponseStatus(code = HttpStatus.OK)
    public ProductDto getProduct(@PathVariable long productId) {
        try {
            return service.get(productId);
        } catch (NoSuchProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{productId}/{productNewName}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ProductDto renameProduct(@PathVariable long productId, @PathVariable String productNewName) {
        try {
            return service.rename(productId, productNewName);
        } catch (NoSuchProductException | ProductIllegalNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteProduct(@PathVariable long productId) {
        service.delete(productId);
    }
}
