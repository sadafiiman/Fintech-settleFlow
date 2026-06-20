package com.settleFlow.modules.wallet.service;

import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.wallet.dto.request.DepositRequest;
import com.settleFlow.modules.wallet.dto.response.WalletResponse;
import com.settleFlow.modules.wallet.dto.response.WalletTransactionResponse;
import com.settleFlow.modules.wallet.enums.OwnerType;
import com.settleFlow.modules.wallet.enums.TransactionDirection;
import com.settleFlow.modules.wallet.enums.TransactionType;
import com.settleFlow.modules.wallet.model.Wallet;
import com.settleFlow.modules.wallet.model.WalletTransaction;
import com.settleFlow.modules.wallet.repository.WalletRepository;
import com.settleFlow.modules.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final CustomerService customerService;   // customer domain — via service

    // ── Get or auto-create a wallet for any owner (customer or merchant) ────
    @Transactional
    public Wallet getOrCreateWallet(Long ownerId, OwnerType ownerType) {
        return walletRepository.findByOwnerIdAndOwnerType(ownerId, ownerType)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder()
                                .ownerId(ownerId)
                                .ownerType(ownerType)
                                .currency("USD")
                                .active(true)
                                .build()
                ));
    }

    // ── Customer: view my wallet + computed balance ─────────────────────────
    public WalletResponse getMyWallet(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Wallet wallet = getOrCreateWallet(customer.getId(), OwnerType.CUSTOMER);
        BigDecimal balance = transactionRepository.calculateBalance(wallet.getId());
        return WalletResponse.from(wallet, balance);
    }

    // ── Customer: deposit funds (e.g. top-up via card — simulated here) ─────
    @Transactional
    public WalletResponse deposit(Long userId, DepositRequest request) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Wallet wallet = getOrCreateWallet(customer.getId(), OwnerType.CUSTOMER);

        credit(wallet.getId(), request.getAmount(), TransactionType.DEPOSIT,
                "MANUAL_DEPOSIT", null,
                request.getDescription() != null ? request.getDescription() : "Wallet top-up");

        BigDecimal balance = transactionRepository.calculateBalance(wallet.getId());
        return WalletResponse.from(wallet, balance);
    }

    // ── Customer: full transaction history ───────────────────────────────────
    public List<WalletTransactionResponse> getMyTransactions(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Wallet wallet = getOrCreateWallet(customer.getId(), OwnerType.CUSTOMER);
        return transactionRepository.findAllByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream().map(WalletTransactionResponse::from).toList();
    }

    // ── Internal — used by other domains (e.g. order domain paying installments,
    //    future payout domain crediting merchants) ───────────────────────────

    @Transactional
    public void credit(Long walletId, BigDecimal amount, TransactionType type,
                       String referenceType, Long referenceId, String description) {
        validatePositive(amount);
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(walletRepository.getReferenceById(walletId))
                .type(type)
                .direction(TransactionDirection.CREDIT)
                .amount(amount)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .build();
        transactionRepository.save(tx);
    }

    @Transactional
    public void debit(Long walletId, BigDecimal amount, TransactionType type,
                      String referenceType, Long referenceId, String description) {
        validatePositive(amount);

        BigDecimal balance = transactionRepository.calculateBalance(walletId);
        if (balance.compareTo(amount) < 0) {
            throw BusinessException.badRequest(
                    String.format("Insufficient wallet balance. Available: %s, Required: %s",
                            balance, amount));
        }

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(walletRepository.getReferenceById(walletId))
                .type(type)
                .direction(TransactionDirection.DEBIT)
                .amount(amount)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .build();
        transactionRepository.save(tx);
    }

    // Convenience — lets other domains debit/credit without fetching the Wallet entity
    public Long getWalletIdForCustomer(Long customerId) {
        return getOrCreateWallet(customerId, OwnerType.CUSTOMER).getId();
    }

    public Long getWalletIdForMerchant(Long merchantId) {
        return getOrCreateWallet(merchantId, OwnerType.MERCHANT).getId();
    }

    private void validatePositive(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.badRequest("Amount must be greater than zero");
        }
    }
}