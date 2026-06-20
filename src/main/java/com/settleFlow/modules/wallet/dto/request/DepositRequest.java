package com.settleFlow.modules.wallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class DepositRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "Deposit amount must be greater than zero")
    private BigDecimal amount;

    private String description;
}