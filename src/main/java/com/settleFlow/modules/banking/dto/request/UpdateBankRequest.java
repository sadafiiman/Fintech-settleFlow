package com.settleFlow.modules.banking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class UpdateBankRequest {

    @DecimalMin(value = "100.00")
    private BigDecimal defaultCreditLimit;

    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1.0")
    private BigDecimal interestRate;

    @Min(1)
    private Integer minInstallmentMonths;

    @Min(1) @Max(60)
    private Integer maxInstallmentMonths;

    private Boolean active;
}