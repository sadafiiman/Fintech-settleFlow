package com.settleFlow.modules.wallet.enums;

public enum TransactionType {
    DEPOSIT,              // funds added to wallet
    INSTALLMENT_PAYMENT,  // customer pays an installment from wallet
    MERCHANT_PAYOUT,      // merchant receives settlement funds (payout domain)
    REFUND,
    FEE,
    ADJUSTMENT            // manual admin correction
}