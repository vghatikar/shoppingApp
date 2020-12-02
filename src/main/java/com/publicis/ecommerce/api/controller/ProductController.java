package com.publicis.ecommerce.api.controller;

import com.publicis.ecommerce.dao.ProductRepository;
import com.publicis.ecommerce.entity.Product;
import com.publicis.ecommerce.entity.User;
import com.publicis.ecommerce.exception.NotFoundException;
import com.publicis.ecommerce.service.ProductService;
import com.publicis.ecommerce.service.SecurityService;
import com.publicis.ecommerce.api.assembler.ProductResourceAssembler;
import com.sipios.springsearch.anotation.SearchSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * API Endpoint for product management
 *
 */
@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    private ProductService productService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ProductResourceAssembler productResourceAssembler;
    @Autowired
    private PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> retrieveAllProducts(Pageable pageable) {
        // Getting all products in application...
        final Page<Product> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(pagedResourcesAssembler.toResource(products, productResourceAssembler));
    }

    @RequestMapping(path = "find", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> searchForProducts(@SearchSpec Specification<Product> specs) {
        return new ResponseEntity<>(productRepository.findAll(Specification.where(specs)), HttpStatus.OK);

    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> retrieveProduct(@PathVariable Long id) {
        // Getting the requiring product; or throwing exception if not found
        final Product product = productService.getProductById(id)
            .orElseThrow(() -> new NotFoundException("product"));

        return ResponseEntity.ok(productResourceAssembler.toResource(product));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDto request) {
        // Gets the current logged-in User...
        final User user = securityService.getCurrentUser();
        // Creating a new product in the application...
        final Product product = productService.createProduct(request.getName(), request.getCurrency(), request.getPrice(), user);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResourceAssembler.toResource(product));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto request) {
        // Getting the requiring product; or throwing exception if not found
        final Product product = productService.getProductById(id)
            .orElseThrow(() -> new NotFoundException("product"));

        // Updating a product in the application...
        productService.updateProduct(product, request.getName(), request.getCurrency(), request.getPrice());

        return ResponseEntity.ok(productResourceAssembler.toResource(product));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        // Getting the requiring product; or throwing exception if not found
        final Product product = productService.getProductById(id)
            .orElseThrow(() -> new NotFoundException("product"));

        // Deleting product from the application...
        productService.deleteProduct(product);

        return ResponseEntity.noContent().build();
    }


    static class ProductDto {
        @NotNull(message = "name is required")
        @Size(message = "name must be equal to or lower than 300", min = 1, max = 300)
        private String name;
        @NotNull
        @Size(message = "Currency must be in ISO 4217 format", min = 3, max = 3)
        private String currency;
        @NotNull(message = "name is required")
        @Min(0)
        private Double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

}
