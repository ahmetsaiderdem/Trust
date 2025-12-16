package com.example.trust.cart;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemRowMapper implements RowMapper<CartItem> {

    @Override
    public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItem c = new CartItem();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setProductId(rs.getInt("product_id"));
        c.setQuantity(rs.getInt("quantity"));
        return c;
    }

}