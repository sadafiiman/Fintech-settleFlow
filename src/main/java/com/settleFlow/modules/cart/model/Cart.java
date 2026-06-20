package com.settleFlow.modules.cart.model;

import com.settleFlow.modules.cart.enums.CartStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_carts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-domain — plain Long, no FK to customer.Customer
    @Column(nullable = false)
    private Long customerId;

    // Set only at checkout time, when the customer picks which bank finances the purchase
    private Long bankId;

    // Set only at checkout time
    private Integer installmentMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CartStatus status = CartStatus.OPEN;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}