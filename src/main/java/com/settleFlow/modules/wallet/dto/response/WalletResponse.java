package com.settleFlow.modules.wallet.dto.response;

import com.settleFlow.modules.wallet.enums.OwnerType;
import com.settleFlow.modules.wallet.model.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class WalletResponse {

    private Long id;
    private Long ownerId;
    private OwnerType ownerType;
    private String currency;
    private BigDecimal balance;   // derived, computed at request time
    private Boolean active;

    public static WalletResponse from(Wallet wallet, BigDecimal balance) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .ownerId(wallet.getOwnerId())
                .ownerType(wallet.getOwnerType())
                .currency(wallet.getCurrency())
                .balance(balance)
                .active(wallet.getActive())
                .build();
    }
}