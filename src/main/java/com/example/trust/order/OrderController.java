package com.example.trust.order;


import com.example.trust.order.dto.OrderDetailResponse;
import com.example.trust.order.dto.OrderSummaryResponse;
import com.example.trust.security.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service){
        this.service=service;
    }

    @GetMapping
    public List<OrderSummaryResponse> list(){
        int userId= SecurityUtils.requiredUserId();
        return service.listByUser(userId);
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse detail(@PathVariable int orderId){
        int userId=SecurityUtils.requiredUserId();
        return service.getDetailForUser(userId,orderId);
    }
}
