package com.example.trust.cart;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository repo;

    public CartService(CartRepository repo){
        this.repo=repo;
    }

    public void add(int userId,int productId,int quantity){
        repo.addOrIncrease(userId,productId,quantity);
    }

    public List<CartItem> get(int userId){
        return repo.findByUserId(userId);
    }

    public void remove(int userId,int productId){
        repo.removeItem(userId,productId);
    }

    public void clear(int userId){
        repo.clear(userId);
    }

}
