package com.settleFlow.modules.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubmitCartRequest {

    @NotNull(message = "Bank ID is required")
    private Long bankId;

    @NotNull
    @Min(value = 1, message = "Installment months must be at least 1")
    private Integer installmentMonths;
}