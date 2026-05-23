package com.settleFlow.modules.banking.controller;

import com.settleFlow.modules.banking.dto.request.AssignCreditRequest;
import com.settleFlow.modules.banking.dto.response.CustomerCreditResponse;
import com.settleFlow.modules.banking.service.CreditService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    // GET /api/credits/my — customer views their own credit lines
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<CustomerCreditResponse>>> myCredits() {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Credit lines retrieved",
                        creditService.getCustomerCredits(customerId)));
    }

    // GET /api/credits/my/bank/{bankId} — credit with one specific bank
    @GetMapping("/my/bank/{bankId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerCreditResponse>> myBankCredit(
            @PathVariable Long bankId) {
        Long customerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Credit retrieved",
                        creditService.getCustomerCreditForBank(customerId, bankId)));
    }

    // POST /api/credits/assign — admin assigns credit to a customer
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerCreditResponse>> assignCredit(
            @Valid @RequestBody AssignCreditRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Credit assigned",
                        creditService.assignCredit(request)));
    }
}