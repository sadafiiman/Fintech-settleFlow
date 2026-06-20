package com.settleFlow.modules.order.dto.response;

import com.settleFlow.modules.order.enums.InstallmentStatus;
import com.settleFlow.modules.order.model.Installment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder
public class InstallmentResponse {

    private Long id;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDateTime paidAt;
    private InstallmentStatus status;

    public static InstallmentResponse from(Installment i) {
        return InstallmentResponse.builder()
                .id(i.getId())
                .installmentNumber(i.getInstallmentNumber())
                .dueDate(i.getDueDate())
                .amount(i.getAmount())
                .paidAmount(i.getPaidAmount())
                .paidAt(i.getPaidAt())
                .status(i.getStatus())
                .build();
    }
}