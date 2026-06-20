package com.settleFlow.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PayInstallmentRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;   // e.g. "WALLET", "CARD" — wallet domain will consume this next
}