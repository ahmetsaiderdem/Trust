package com.example.trust.product;

import com.example.trust.product.dto.PageResponse;
import com.example.trust.product.dto.ProductCreateRequest;
import com.example.trust.product.dto.ProductListItem;
import com.example.trust.product.dto.ProductUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.trust.product.dto.ProductDetailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
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
    public PageResponse<ProductListItem> search(
            String q,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            String sort,
            int page,
            int size
    ) {
        ProductQuery pq = new ProductQuery();
        pq.q = q;
        pq.categoryId = categoryId;
        pq.minPrice = minPrice;
        pq.maxPrice = maxPrice;
        pq.inStock = inStock;
        pq.sort = sort;
        pq.page = page;
        pq.size = size;

        var result = repo.search(pq);

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));

        return new PageResponse<>(result.items(), safePage, safeSize, result.total());
    }

    public ProductDetailResponse getDetail(int id) {
        return repo.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public List<ProductListItem> related(int productId,int limit){
        int safeLimit=Math.max(1,Math.min(limit,20));

        if (repo.findById(productId).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found");
        }

        Integer categoryId=repo.findCategoryIdByProductId(productId);

        if (categoryId != null){
            List<ProductListItem> sameCat=repo.findRelatedByCategory(categoryId,productId,safeLimit);

            if (!sameCat.isEmpty()){
                return sameCat;
            }

        }
        return repo.findRelatedFallbackNewest(productId,safeLimit);
    }

}
