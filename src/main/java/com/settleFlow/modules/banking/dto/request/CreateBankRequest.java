package com.settleFlow.modules.banking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateBankRequest {

    @NotBlank(message = "Bank name is required")
    private String name;

    @NotNull
    @DecimalMin(value = "100.00", message = "Minimum credit limit is 100")
    private BigDecimal defaultCreditLimit;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1.0", message = "Interest rate must be between 0 and 1 (e.g. 0.15 = 15%)")
    private BigDecimal interestRate;

    @NotNull
    @Min(value = 1, message = "Minimum installment months must be at least 1")
    private Integer minInstallmentMonths;

    @NotNull
    @Min(value = 1)
    @Max(value = 60, message = "Maximum installment months cannot exceed 60")
    private Integer maxInstallmentMonths;
}