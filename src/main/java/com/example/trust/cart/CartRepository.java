package com.example.trust.cart;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CartRepository {
    private final JdbcTemplate jdbc;
    private final CartItemRowMapper mapper=new CartItemRowMapper();

    public CartRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;

    }

    public void addOrIncrease(int userId, int productId, int quantity) {
        String sql =
                "INSERT INTO cart_items(user_id, product_id, quantity) VALUES (?,?,?) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        jdbc.update(sql, userId, productId, quantity, quantity);
    }


    public List<CartItem> findByUserId(int userId){
        String sql = "SELECT id, user_id, product_id, quantity FROM cart_items WHERE user_id=? ORDER BY id DESC";
        return jdbc.query(sql, mapper, userId);
    }

    public int removeItem(int userId,int productId){
        return jdbc.update("DELETE FROM cart_items WHERE user_id=? AND product_id=?",userId,productId);
    }

    public int clear(int userId){
        return jdbc.update("DELETE FROM cart_items WHERE user_id=?", userId);
    }
    
}
