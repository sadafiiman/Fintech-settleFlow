package com.settleFlow.modules.wallet.model;

import com.settleFlow.modules.wallet.enums.TransactionDirection;
import com.settleFlow.modules.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Immutable ledger entry — once written, never updated or deleted.
// This is what makes the wallet auditable: the balance is just math over
// these rows, never a mutable number that can drift or be corrupted.
@Entity
@Table(name = "wallet_transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Same-domain FK — Wallet and WalletTransaction both live in the wallet domain
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionDirection direction;

    // Always stored positive — direction determines the sign in balance math
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // What triggered this entry — e.g. "INSTALLMENT", "ORDER", "MANUAL_DEPOSIT"
    private String referenceType;

    // Cross-domain ID of the thing that triggered it (e.g. installmentId)
    private Long referenceId;

    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}