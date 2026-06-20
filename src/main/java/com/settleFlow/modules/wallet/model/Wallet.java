package com.settleFlow.modules.wallet.model;

import com.settleFlow.modules.wallet.enums.OwnerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_wallets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "owner_type"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-domain — plain Long. Could be a customerId or a merchantId
    // depending on ownerType. No FK to either domain.
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false)
    private OwnerType ownerType;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Deliberately NO balance field — balance is always derived from
    // WalletTransactionRepository.calculateBalance(walletId)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}