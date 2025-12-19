package com.example.trust.cart;

import com.example.trust.cart.dto.AddCartRequest;
import com.example.trust.checkout.CheckoutService;
import com.example.trust.security.SecurityUtils;
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
        int userId= SecurityUtils.requiredUserId();
        service.add(userId,req.getProductId(),req.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CartItem> get(){
        int userId = SecurityUtils.requiredUserId();
        return service.get(userId);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> remove(@RequestParam int productId){
        int userId=SecurityUtils.requiredUserId();
        service.remove(userId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(){
       int userId=SecurityUtils.requiredUserId();
        service.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(){
        int userId  =SecurityUtils.requiredUserId();
        int orderId= checkoutService.checkout(userId);
        return ResponseEntity.status(201).body(java.util.Map.of("orderId",orderId));
    }

    @PatchMapping("/items/decrease")
    public ResponseEntity<Void> decrease(@RequestParam int productId){
        int userId = SecurityUtils.requiredUserId();
        service.decrease(userId, productId);
        return ResponseEntity.noContent().build();
    }


}
