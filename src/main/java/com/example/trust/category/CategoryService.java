package com.example.trust.category;

import com.example.trust.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo){
        this.repo=repo;
    }

    public List<CategoryResponse> list(){
        return repo.findAll();
    }
}
