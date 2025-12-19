package com.example.trust.product;
import com.example.trust.product.dto.ProductDetailResponse;
import com.example.trust.product.dto.ProductListItem;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public int update(int id, Product p ){
        String sql = "UPDATE products SET name=?, price=?, stock=?, active=? WHERE id = ?";
        return jdbc.update(sql, p.getName(), p.getPrice(), p.getStock(), p.isActive(), id);
    }


    public int delete(int id){
        return jdbc.update("DELETE FROM products WHERE id=?",id);
    }

    public BigDecimal getPriceById(int productId) {
        String sql = "SELECT price FROM products WHERE id = ?";
        return jdbc.queryForObject(sql, (rs, rowNum) -> rs.getBigDecimal("price"), productId);
    }

    public int decreaseStockIfEnough(int productId,int quantity){
        String sql="UPDATE products " +
                "SET stock = stock - ? " +
                "WHERE id = ? AND active = 1 AND stock >= ?";
        return jdbc.update(sql,quantity,productId,quantity);
    }

    public record ProductPage(List<ProductListItem> items,long total){ }

    public ProductPage search(ProductQuery query){
        String baseFrom=" FROM products p " +
                "LEFT JOIN categories c ON c.id = p.category_id ";

        StringBuilder where=new StringBuilder(" WHERE 1=1 ");
        List<Object> params= new ArrayList<>();

        where.append(" AND p.active = 1 ");

        if (query.q != null && !query.q.isBlank()) {
            where.append(" AND (LOWER(p.name) LIKE ? ) ");
            params.add("%" + query.q.toLowerCase() + "%");
        }

        if (query.categoryId != null) {
            where.append(" AND p.category_id = ? ");
            params.add(query.categoryId);
        }

        if (query.minPrice != null) {
            where.append(" AND p.price >= ? ");
            params.add(query.minPrice);
        }

        if (query.maxPrice != null) {
            where.append(" AND p.price <= ? ");
            params.add(query.maxPrice);
        }

        if (Boolean.TRUE.equals(query.inStock)) {
            where.append(" AND p.stock > 0 ");
        }

        // total
        long total = jdbc.queryForObject(
                "SELECT COUNT(*) " + baseFrom + where,
                Long.class,
                params.toArray()
        );

        String orderBy = " ORDER BY p.id DESC ";
        if ("priceAsc".equalsIgnoreCase(query.sort)) orderBy = " ORDER BY p.price ASC ";
        if ("priceDesc".equalsIgnoreCase(query.sort)) orderBy = " ORDER BY p.price DESC ";

        int size = Math.max(1, Math.min(query.size, 100));
        int page = Math.max(query.page, 0);
        int offset = page * size;

        String sql =
                "SELECT p.id, p.name, p.price, p.stock, p.active, p.category_id, c.name AS category_name " +
                        baseFrom + where +
                        orderBy +
                        " LIMIT ? OFFSET ?";

        List<Object> listParams = new ArrayList<>(params);
        listParams.add(size);
        listParams.add(offset);

        List<ProductListItem> items = jdbc.query(sql, (rs, rn) -> {
            ProductListItem p = new ProductListItem();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setStock(rs.getInt("stock"));
            p.setActive(rs.getBoolean("active"));
            int cid = rs.getInt("category_id");
            p.setCategoryId(rs.wasNull() ? null : cid);
            p.setCategoryName(rs.getString("category_name"));
            return p;
        }, listParams.toArray());

        return new ProductPage(items, total);
    }

    public Optional<ProductDetailResponse> findDetailById(int id) {
        String sql = """
        SELECT p.id, p.name, p.price, p.stock, p.active,
               p.category_id, c.name AS category_name
        FROM products p
        LEFT JOIN categories c ON c.id = p.category_id
        WHERE p.id = ?
        """;

        List<ProductDetailResponse> list = jdbc.query(sql, (rs, rn) -> {
            ProductDetailResponse d = new ProductDetailResponse();
            d.setId(rs.getInt("id"));
            d.setName(rs.getString("name"));
            d.setPrice(rs.getBigDecimal("price"));
            d.setStock(rs.getInt("stock"));
            d.setActive(rs.getBoolean("active"));

            int cid = rs.getInt("category_id");
            d.setCategoryId(rs.wasNull() ? null : cid);
            d.setCategoryName(rs.getString("category_name"));
            return d;
        }, id);

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Integer findCategoryIdByProductId(int productId){
        String sql="SELECT category_id FROM products WHERE id = ?";
        List<Integer> list=jdbc.query(sql,(rs,rn)->{
            int v =rs.getInt("category_id");
            return rs.wasNull() ? null : v;

        },productId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<ProductListItem> findRelatedByCategory(int categorId,int excludeProductId,int limit){
        String sql= """
                SELECT p.id, p.name, p.price, p.stock, p.active, p.category_id, c.name AS category_name
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE p.active =1
                AND p.category_id = ?
                AND p.id <> ?
                ORDER BY p.id DESC
                LIMIT ?
                """;

        return jdbc.query(sql,(rs,rn)->{
            ProductListItem p=new ProductListItem();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setStock(rs.getInt("stock"));
            p.setActive(rs.getBoolean("active"));

            int cid=rs.getInt("category_id");
            p.setCategoryId(rs.wasNull() ? null : cid);
            p.setCategoryName(rs.getString("category_name"));
            return p;

        },categorId,excludeProductId,limit);
    }

    public List<ProductListItem> findRelatedFallbackNewest(int excludeProductId,int limit){
        String sql= """
                SELECT p.id, p.name, p.price, p.stock, p.active, p.category_id, c.name AS category_name
                FROM products p
                LEFT JOIN categories c ON c.id = p.category_id
                WHERE p.active = 1
                AND p.id <> ?
                ORDER BY p.id DESC
                LIMIT ?
                """;

        return jdbc.query(sql,(rs,rn)->{
            ProductListItem p= new ProductListItem();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setPrice(rs.getBigDecimal("price"));
            p.setStock(rs.getInt("stock"));
            p.setActive(rs.getBoolean("active"));

            int cid=rs.getInt("category_id");
            p.setCategoryId(rs.wasNull() ? null : cid);
            p.setCategoryName(rs.getString("category_name"));

            return p;

        },excludeProductId,limit);
    }


}
