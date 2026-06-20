package com.settleFlow.modules.order.enums;

public enum InstallmentStatus {
    PENDING,   // not yet due, or due but not paid
    PAID,
    OVERDUE    // past due date, unpaid
}
