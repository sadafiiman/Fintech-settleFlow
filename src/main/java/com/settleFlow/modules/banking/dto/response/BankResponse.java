package com.settleFlow.modules.banking.dto.response;

import com.settleFlow.modules.banking.model.Bank;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class BankResponse {

    private Long id;
    private String name;
    private BigDecimal defaultCreditLimit;
    private BigDecimal interestRate;
    private Integer minInstallmentMonths;
    private Integer maxInstallmentMonths;
    private Boolean active;

    public static BankResponse from(Bank bank) {
        return BankResponse.builder()
                .id(bank.getId())
                .name(bank.getName())
                .defaultCreditLimit(bank.getDefaultCreditLimit())
                .interestRate(bank.getInterestRate())
                .minInstallmentMonths(bank.getMinInstallmentMonths())
                .maxInstallmentMonths(bank.getMaxInstallmentMonths())
                .active(bank.getActive())
                .build();
    }
}