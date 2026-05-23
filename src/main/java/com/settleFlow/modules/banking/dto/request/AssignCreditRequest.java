package com.settleFlow.modules.banking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class AssignCreditRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long bankId;

    // If null, use the bank's defaultCreditLimit
    @DecimalMin(value = "100.00")
    private BigDecimal customLimit;
}