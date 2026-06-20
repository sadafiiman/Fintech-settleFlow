package com.settleFlow.modules.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "cart_items",
        // One row per product per cart — adding the same product again increases quantity
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Same-domain FK — Cart and CartItem both belong to the cart domain
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Cross-domain — plain Long, no FK to merchant.Product
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // Snapshot fields — captured at the moment the item is added.
    // If the merchant changes the price later, the cart keeps the price the
    // customer saw when they added it (standard e-commerce behaviour).
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}