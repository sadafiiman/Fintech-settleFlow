package com.settleFlow.modules.order.model;

import com.settleFlow.modules.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-domain — plain Longs, no FK constraints to customer/banking/cart domains
    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long bankId;

    // Kept for traceability only — order no longer depends on the cart domain
    // at read-time (OrderItem snapshots replace the need to re-query the cart)
    private Long sourceCartId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;   // cost of goods, before interest

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInterest;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;       // principal + interest = sum of all installments

    @Column(nullable = false)
    private Integer installmentMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}