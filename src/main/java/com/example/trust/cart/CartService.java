package com.example.trust.cart;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public void remove(int userId, int productId) {
        repo.removeItem(userId, productId);
    }

    public void clear(int userId) {
        repo.clear(userId);
    }

    public void decrease(int userId, int productId) {
        int affected = repo.decreaseOrRemove(userId, productId);
        if (affected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
    }



}
