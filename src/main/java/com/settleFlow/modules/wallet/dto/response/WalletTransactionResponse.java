package com.settleFlow.modules.wallet.dto.response;

import com.settleFlow.modules.wallet.enums.TransactionDirection;
import com.settleFlow.modules.wallet.enums.TransactionType;
import com.settleFlow.modules.wallet.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class WalletTransactionResponse {

    private Long id;
    private TransactionType type;
    private TransactionDirection direction;
    private BigDecimal amount;
    private String referenceType;
    private Long referenceId;
    private String description;
    private LocalDateTime createdAt;

    public static WalletTransactionResponse from(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .direction(tx.getDirection())
                .amount(tx.getAmount())
                .referenceType(tx.getReferenceType())
                .referenceId(tx.getReferenceId())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}