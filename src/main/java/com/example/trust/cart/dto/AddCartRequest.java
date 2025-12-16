package com.example.trust.cart.dto;

import jakarta.validation.constraints.Min;

public class AddCartRequest {

    @Min(1) private int userId;
    @Min(1) private int productId;
    @Min(1) private int quantity;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
