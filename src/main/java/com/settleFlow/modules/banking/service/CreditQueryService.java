package com.settleFlow.modules.banking.service;

import com.settleFlow.modules.banking.enums.CreditStatus;
import com.settleFlow.modules.banking.model.CustomerCredit;
import com.settleFlow.modules.banking.repository.CustomerCreditRepository;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// Read-only query service — used by cart and order domains
// Having it separate keeps CreditService focused on writes only
@Service
@RequiredArgsConstructor
public class CreditQueryService {

    private final CustomerCreditRepository creditRepository;

    public boolean hasSufficientCredit(Long customerId, Long bankId, BigDecimal amount) {
        return creditRepository
                .findByCustomerIdAndBankId(customerId, bankId)
                .map(c -> c.canAfford(amount))
                .orElse(false);
    }

    public BigDecimal getAvailableCredit(Long customerId, Long bankId) {
        CustomerCredit credit = creditRepository
                .findByCustomerIdAndBankId(customerId, bankId)
                .orElseThrow(() -> new NotFoundException(
                                "No credit line found for this customer and bank"
                        )
                );

        if (credit.getStatus() != CreditStatus.ACTIVE) {
            return BigDecimal.ZERO;
        }

        return credit.getAvailableAmount();
    }
}