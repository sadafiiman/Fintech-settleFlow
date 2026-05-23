package com.settleFlow.modules.banking.model;

import com.settleFlow.modules.banking.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "banking_customer_credits",
        // A customer can only have ONE credit line per bank
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "bank_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No @ManyToOne to Customer — cross-domain, we store the ID only
    @Column(nullable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal usedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CreditStatus status = CreditStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public BigDecimal getAvailableAmount() {
        return creditLimit.subtract(usedAmount);
    }

    public boolean canAfford(BigDecimal amount) {
        return status == CreditStatus.ACTIVE
                && getAvailableAmount().compareTo(amount) >= 0;
    }
}