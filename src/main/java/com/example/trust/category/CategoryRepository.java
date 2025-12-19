package com.example.trust.category;

import com.example.trust.category.dto.CategoryResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbc;

    public CategoryRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public List<CategoryResponse> findAll(){
        String sql="SELECT id, name, slug, parent_id FROM categories ORDER BY name";

        return jdbc.query(sql,(rs,rn) ->{
            CategoryResponse c=new CategoryResponse();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            c.setSlug(rs.getString("slug"));
            int p =rs.getInt("parent_id");
            c.setParentId(rs.wasNull()? null :p);
            return c;
        });
    }
}
