package com.example.trust.order;


import com.example.trust.order.dto.OrderDetailResponse;
import com.example.trust.order.dto.OrderSummaryResponse;
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
    public List<OrderSummaryResponse> list(@RequestParam int userId){
        return service.listByUser(userId);
    }

    @GetMapping("/{orderId}")
    public OrderDetailResponse detail(@PathVariable int orderId){
        return service.getDetail(orderId);
    }
}
