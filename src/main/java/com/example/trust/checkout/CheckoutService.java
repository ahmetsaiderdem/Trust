package com.example.trust.checkout;

import com.example.trust.cart.CartItem;
import com.example.trust.cart.CartRepository;
import com.example.trust.order.OrderRepository;
import com.example.trust.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    public CheckoutService(CartRepository cartRepo,ProductRepository productRepo,OrderRepository orderRepo){
        this.cartRepo=cartRepo;
        this.productRepo=productRepo;
        this.orderRepo=orderRepo;

    }


    @Transactional
    public int checkout(int userId) {
        List<CartItem> cart = cartRepo.findByUserId(userId);
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        BigDecimal total = BigDecimal.ZERO;


        for (CartItem item : cart) {
            BigDecimal unitPrice = productRepo.getPriceById(item.getProductId());
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        int orderId = orderRepo.insertOrder(userId, total, "NEW");


        for (CartItem item : cart) {
            BigDecimal unitPrice = productRepo.getPriceById(item.getProductId());

            int updated = productRepo.decreaseStockIfEnough(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException(
                        "Insufficient stock for productId=" + item.getProductId()
                );
            }

            orderRepo.insertOrderItem(orderId, item.getProductId(), item.getQuantity(), unitPrice);
        }

        cartRepo.clear(userId);
        return orderId;
    }


}
