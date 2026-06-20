package com.settleFlow.modules.cart.enums;

public enum CartStatus {
    OPEN,         // customer is adding/removing items
    SUBMITTED,    // checkout in progress (transient — used during processing)
    APPROVED,     // credit deducted, stock reserved — ready for order creation
    CANCELLED,
    CONVERTED
}