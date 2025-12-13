package com.example.trust.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbc;
    private final ProductRowMapper mapper = new ProductRowMapper();

    public ProductRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Product> findAll() {
        String sql = "SELECT id, name, price, stock, active FROM products";
        return jdbc.query(sql, mapper);
    }

    public int insert(Product p) {
        String sql = "INSERT INTO products(name,price,stock,active) VALUES (?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setBoolean(4, p.isActive());
            return ps;
        }, kh);

        return kh.getKey().intValue();
    }

    public Optional<Product> findById(int id){
        String sql= "SELECT id, name, price, stock, active FROM products WHERE id = ?";
        List<Product> list=jdbc.query(sql,mapper,id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public int update(int id,Product p ){
        String sql="UPDATE products SET name=?, price=?, stock=?, active=? WHERE id = ?";
        return jdbc.update(sql,p.getName(), p.getPrice(), p.getStock(), p.isActive());

    }

    public int delete(int id){
        return jdbc.update("DELETE FROM products WHERE id=?",id);
    }
}
