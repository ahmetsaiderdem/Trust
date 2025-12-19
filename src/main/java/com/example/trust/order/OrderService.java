package com.example.trust.order;

import com.example.trust.order.dto.OrderDetailResponse;
import com.example.trust.order.dto.OrderSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo){
        this.repo=repo;

    }

    public List<OrderSummaryResponse> listByUser(int userId){
        return repo.findByUserId(userId);
    }

    public OrderDetailResponse getDetailForUser(int userId,int orderId){
        var headerOpt= repo.findOrderHeader(orderId);
        if (headerOpt.isEmpty() || headerOpt.get().getUserId() != userId){
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found");
        }

        var header=headerOpt.get();
        var items=repo.findItems(orderId);

        var detail=new OrderDetailResponse();
        detail.setId(header.getId());
        detail.setUserId(header.getUserId());
        detail.setTotalAmount(header.getTotalAmount());
        detail.setStatus(header.getStatus());
        detail.setCreatedAt(header.getCreatedAt());
        detail.setItems(items);
        return detail;
    }
}
