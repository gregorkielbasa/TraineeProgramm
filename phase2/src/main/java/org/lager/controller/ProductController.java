package org.lager.controller;

import org.lager.model.dto.ProductDto;
import org.lager.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Long> getAllIds() {
        return service.getAllIds();
    }

    @PostMapping("/{newProductName}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ProductDto createProduct(@PathVariable String newProductName) {
        return service.create(newProductName);
    }

    @GetMapping("/{productId}/")
    @ResponseStatus(code = HttpStatus.OK)
    public ProductDto getProduct(@PathVariable long productId) {
        return service.get(productId);
    }

    @PostMapping("/{productId}/{productNewName}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ProductDto renameProduct(@PathVariable long productId, @PathVariable String productNewName) {
        return service.rename(productId, productNewName);
    }

    @DeleteMapping("/{productId}/")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteProduct(@PathVariable long productId) {
        service.delete(productId);
    }
}
