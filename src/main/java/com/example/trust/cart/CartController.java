package com.example.trust.cart;

import com.example.trust.cart.dto.AddCartRequest;
import com.example.trust.checkout.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;
    private final CheckoutService checkoutService;

    public CartController(CartService service,CheckoutService checkoutService){
        this.service=service;
        this.checkoutService=checkoutService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> add(@Valid @RequestBody AddCartRequest req){
        service.add(req.getUserId(),req.getProductId(),req.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public List<CartItem> get(@PathVariable int userId){
        return service.get(userId);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> remove(@RequestParam int userId,@RequestParam int productId){
        service.remove(userId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clear(@PathVariable int userId){
        service.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable int userId){
        int orderId= checkoutService.checkout(userId);
        return ResponseEntity.status(201).body(java.util.Map.of("orderId",orderId));
    }
}
