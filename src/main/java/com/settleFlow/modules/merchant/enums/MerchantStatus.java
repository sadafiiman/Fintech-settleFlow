package com.settleFlow.modules.merchant.enums;

public enum MerchantStatus {
    PENDING,    // registered, waiting for admin approval
    ACTIVE,     // approved, can sell
    SUSPENDED,  // temporarily blocked by admin
    REJECTED    // application denied
}