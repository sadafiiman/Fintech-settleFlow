package com.settleFlow.modules.order.enums;

public enum OrderStatus {
    ACTIVE,       // installments are running
    COMPLETED,    // all installments paid
    CANCELLED,    // cancelled before completion — credit/stock released
    DEFAULTED     // customer failed to pay — reserved for future collections logic
}