package com.example.trust.product;


import com.example.trust.product.dto.PageResponse;
import com.example.trust.product.dto.ProductCreateRequest;
import com.example.trust.product.dto.ProductListItem;
import com.example.trust.product.dto.ProductUpdateRequest;
import com.example.trust.product.dto.ProductDetailResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {



    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;

    }

    @GetMapping("/all")
    public List<Product> listAll(){
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

    @GetMapping
    public PageResponse<ProductListItem> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return productService.search(q, categoryId, minPrice, maxPrice, inStock, sort, page, size);
    }

    @GetMapping("/{id}/detail")
    public ProductDetailResponse detail(@PathVariable int id){
        return productService.getDetail(id);
    }

    @GetMapping("/{id}/related")
    public List<ProductListItem> related(
            @PathVariable int id,
            @RequestParam(defaultValue= "6") int limit
    ){
        return productService.related(id, limit);
    }

}
