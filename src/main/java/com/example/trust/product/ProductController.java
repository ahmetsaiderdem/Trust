package com.example.trust.product;


import com.example.trust.product.dto.ProductCreateRequest;
import com.example.trust.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @GetMapping
    public List<Product> list(){
        return productService.list();
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductCreateRequest req){
        return ResponseEntity.status(201).body(productService.create(req));
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable int id){
        return productService.get(id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable int id, @Valid @RequestBody ProductUpdateRequest req){
        return productService.update(id,req);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
