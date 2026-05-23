package com.settleFlow.modules.banking.dto.response;

import com.settleFlow.modules.banking.enums.CreditStatus;
import com.settleFlow.modules.banking.model.CustomerCredit;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class CustomerCreditResponse {

    private Long id;
    private Long customerId;
    private String bankName;
    private BigDecimal creditLimit;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;
    private CreditStatus status;

    public static CustomerCreditResponse from(CustomerCredit cc) {
        return CustomerCreditResponse.builder()
                .id(cc.getId())
                .customerId(cc.getCustomerId())
                .bankName(cc.getBank().getName())
                .creditLimit(cc.getCreditLimit())
                .usedAmount(cc.getUsedAmount())
                .availableAmount(cc.getAvailableAmount())
                .status(cc.getStatus())
                .build();
    }
}