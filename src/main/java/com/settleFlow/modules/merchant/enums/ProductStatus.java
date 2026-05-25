package com.settleFlow.modules.merchant.enums;

public enum ProductStatus {
    ACTIVE,        // visible in marketplace
    OUT_OF_STOCK,  // stock = 0, still visible
    INACTIVE,      // merchant hid it
    DELETED        // soft deleted
}