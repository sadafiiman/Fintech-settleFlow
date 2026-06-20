package com.settleFlow.modules.order.dto.response;

import com.settleFlow.modules.order.enums.OrderStatus;
import com.settleFlow.modules.order.model.Order;
import com.settleFlow.modules.order.model.OrderItem;
import com.settleFlow.modules.order.model.Installment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class OrderResponse {

    private Long id;
    private Long bankId;
    private BigDecimal principalAmount;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private Integer installmentMonths;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private List<InstallmentResponse> installments;

    public static OrderResponse from(Order order, List<OrderItem> items,
                                     List<Installment> installments) {
        return OrderResponse.builder()
                .id(order.getId())
                .bankId(order.getBankId())
                .principalAmount(order.getPrincipalAmount())
                .totalInterest(order.getTotalInterest())
                .totalAmount(order.getTotalAmount())
                .installmentMonths(order.getInstallmentMonths())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items.stream().map(OrderItemResponse::from).toList())
                .installments(installments.stream().map(InstallmentResponse::from).toList())
                .build();
    }
}