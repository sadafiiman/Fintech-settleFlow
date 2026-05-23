package com.settleFlow.modules.banking.service;

import com.settleFlow.modules.banking.dto.request.AssignCreditRequest;
import com.settleFlow.modules.banking.dto.response.CustomerCreditResponse;
import com.settleFlow.modules.banking.enums.CreditStatus;
import com.settleFlow.modules.banking.model.Bank;
import com.settleFlow.modules.banking.model.CustomerCredit;
import com.settleFlow.modules.banking.repository.CustomerCreditRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CustomerCreditRepository creditRepository;
    private final BankService bankService;

    // ── Admin: assign a credit line to a customer
    @Transactional
    public CustomerCreditResponse assignCredit(AssignCreditRequest request) {
        if (creditRepository.existsByCustomerIdAndBankId(
                request.getCustomerId(), request.getBankId())) {
            throw BusinessException.conflict(
                    "Customer already has a credit line with this bank");
        }

        Bank bank = bankService.findActiveOrThrow(request.getBankId());

        BigDecimal limit = request.getCustomLimit() != null
                ? request.getCustomLimit()
                : bank.getDefaultCreditLimit();

        CustomerCredit credit = CustomerCredit.builder()
                .customerId(request.getCustomerId())
                .bank(bank)
                .creditLimit(limit)
                .usedAmount(BigDecimal.ZERO)
                .status(CreditStatus.ACTIVE)
                .build();

        return CustomerCreditResponse.from(creditRepository.save(credit));
    }

    // ── Customer: view all my credit lines
    public List<CustomerCreditResponse> getCustomerCredits(Long customerId) {
        return creditRepository.findAllByCustomerId(customerId)
                .stream()
                .map(CustomerCreditResponse::from)
                .toList();
    }

    // ── Customer: view credit with a specific bank
    public CustomerCreditResponse getCustomerCreditForBank(Long customerId, Long bankId) {
        return creditRepository.findByCustomerIdAndBankId(customerId, bankId)
                .map(CustomerCreditResponse::from)
                .orElseThrow(() -> new NotFoundException(
                        "No credit line found for this customer and bank"));
    }

    // ── Called by CartCheckoutService when an order is placed
    // Locks the credit (deducts from available) when order is submitted
    @Transactional
    public void deductCredit(Long customerId, Long bankId, BigDecimal amount) {
        CustomerCredit credit = findActiveCredit(customerId, bankId);

        if (!credit.canAfford(amount)) {
            throw BusinessException.badRequest(
                    String.format("Insufficient credit. Available: %s, Required: %s",
                            credit.getAvailableAmount(), amount));
        }

        credit.setUsedAmount(credit.getUsedAmount().add(amount));
        creditRepository.save(credit);
    }

    // ── Called by OrderService when an order is cancelled
    // Releases credit back to available
    @Transactional
    public void releaseCredit(Long customerId, Long bankId, BigDecimal amount) {
        CustomerCredit credit = findActiveCredit(customerId, bankId);

        BigDecimal newUsed = credit.getUsedAmount().subtract(amount);

        // Guard against going negative due to any edge case
        if (newUsed.compareTo(BigDecimal.ZERO) < 0) {
            newUsed = BigDecimal.ZERO;
        }

        credit.setUsedAmount(newUsed);
        creditRepository.save(credit);
    }

    // ── Admin: suspend / reinstate a credit line
    @Transactional
    public CustomerCreditResponse updateStatus(Long creditId, CreditStatus newStatus) {
        CustomerCredit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new NotFoundException("Credit line not found: " + creditId));
        credit.setStatus(newStatus);
        return CustomerCreditResponse.from(creditRepository.save(credit));
    }

    // ── Internal helper
    private CustomerCredit findActiveCredit(Long customerId, Long bankId) {
        CustomerCredit credit = creditRepository
                .findByCustomerIdAndBankId(customerId, bankId)
                .orElseThrow(() -> new NotFoundException(
                        "No credit line found for customer " + customerId
                                + " with bank " + bankId));

        if (credit.getStatus() != CreditStatus.ACTIVE) {
            throw BusinessException.badRequest("Credit line is not active");
        }

        return credit;
    }
}