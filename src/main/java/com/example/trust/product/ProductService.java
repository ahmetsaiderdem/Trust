package com.example.trust.product;

import com.example.trust.product.dto.ProductCreateRequest;
import com.example.trust.product.dto.ProductUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo){
        this.repo=repo;
    }

    public List<Product> list(){
        return repo.findAll();
    }

    public Product create(ProductCreateRequest req){
        Product p=new Product();
        p.setName(req.getName());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setActive(true);

        int id=repo.insert(p);

        p.setId(id);
        return p;
    }

    public Product get(int id){
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found. " + id));

    }

    public Product update(int id, ProductUpdateRequest req){
        Product p=new Product();
        p.setName(req.getName());
        p.setPrice(req.getPrice());
        p.setStock(req.getStock());
        p.setActive(req.isActive());

        int updated= repo.update(id,p);
        if (updated==0)throw  new IllegalArgumentException("Product not found: "+id);
        p.setId(id);
        return p;
    }

    public void delete(int id){
        int deleted= repo.delete(id);
        if (deleted==0)throw new IllegalArgumentException("Product not found: "+ id);
    }
}
