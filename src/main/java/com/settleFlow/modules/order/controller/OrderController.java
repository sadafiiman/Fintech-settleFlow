package com.settleFlow.modules.order.controller;

import com.settleFlow.modules.order.dto.response.OrderResponse;
import com.settleFlow.modules.order.service.OrderService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders — create order from the approved cart
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created", orderService.createFromCart(userId)));
    }

    // GET /api/orders
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> myOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Orders retrieved", orderService.getMyOrders(userId)));
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Order retrieved", orderService.getOrderDetail(userId, id)));
    }

    // POST /api/orders/{id}/cancel
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled"));
    }
}