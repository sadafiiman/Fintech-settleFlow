package com.settleFlow.modules.wallet.controller;

import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import com.settleFlow.modules.wallet.dto.request.DepositRequest;
import com.settleFlow.modules.wallet.dto.response.WalletResponse;
import com.settleFlow.modules.wallet.dto.response.WalletTransactionResponse;
import com.settleFlow.modules.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class WalletController {

    private final WalletService walletService;

    // GET /api/wallet/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<WalletResponse>> myWallet() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Wallet retrieved", walletService.getMyWallet(userId)));
    }

    // POST /api/wallet/deposit
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<WalletResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Deposit successful", walletService.deposit(userId, request)));
    }

    // GET /api/wallet/transactions
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> transactions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Transactions retrieved", walletService.getMyTransactions(userId)));
    }
}