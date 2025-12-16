package com.example.trust.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbc;

    public OrderRepository(JdbcTemplate jdbc){
        this.jdbc=jdbc;
    }

    public int insertOrder(int userId, java.math.BigDecimal totalAmount, String status){
        String sql="INSERT INTO orders(user_id, total_amount, status) VALUES (?,?,?)";
        KeyHolder kh=new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,userId);
            ps.setBigDecimal(2,totalAmount);
            ps.setString(3,status);
            return ps;
        },kh);
        return Objects.requireNonNull(kh.getKey()).intValue();
    }

    public void insertOrderItem(int orderId,int productId,int quantity, java.math.BigDecimal unitprice){
        String sql="INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES (?,?,?,?)";
        jdbc.update(sql,orderId,productId,quantity,unitprice);
    }

    public java.util.List<com.example.trust.order.dto.OrderSummaryResponse> findByUserId(int userId){
        String sql = "SELECT id, user_id, total_amount, status, created_at " +
                "FROM orders WHERE user_id = ? ORDER BY id DESC";
        return jdbc.query(sql,(rs, rowNum) -> {
            var o = new com.example.trust.order.dto.OrderSummaryResponse();
            o.setId(rs.getInt("id"));
            o.setUserId(rs.getInt("user_id"));
            o.setTotalAmount(rs.getBigDecimal("total_amount"));
            o.setStatus(rs.getString("status"));
            o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return o;

        },userId);
    }

    public java.util.Optional<com.example.trust.order.dto.OrderSummaryResponse> findOrderHeader(int orderId){
        String sql="SELECT id, user_id, total_amount, status, created_at FROM orders WHERE id = ?";
        var list=jdbc.query(sql,(rs,rowNum)->{
            var o =new com.example.trust.order.dto.OrderSummaryResponse();
            o.setId(rs.getInt("id"));
            o.setUserId(rs.getInt("user_id"));
            o.setTotalAmount(rs.getBigDecimal("total_amount"));
            o.setStatus(rs.getString("status"));
            o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return o;
        },orderId);
        return list.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(list.get(0));
    }

    public java.util.List<com.example.trust.order.dto.OrderItemResponse> findItems(int orderId){
        String sql="SELECT product_id, quantity, unit_price FROM order_items WHERE order_id = ?";
        return jdbc.query(sql,(rs, rowNum) -> {
            var i =new com.example.trust.order.dto.OrderItemResponse();
            i.setProductId(rs.getInt("product_id"));
            i.setQuantity(rs.getInt("quantity"));
            i.setUnitPrice(rs.getBigDecimal("unit_price"));
            return i;
        },orderId);
    }



}
