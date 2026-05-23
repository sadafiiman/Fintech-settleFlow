package com.settleFlow.modules.banking.controller;

import com.settleFlow.modules.banking.dto.request.CreateBankRequest;
import com.settleFlow.modules.banking.dto.request.UpdateBankRequest;
import com.settleFlow.modules.banking.dto.response.BankResponse;
import com.settleFlow.modules.banking.service.BankService;
import com.settleFlow.modules.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    // GET /api/banks — public, customers and merchants can see active banks
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> listActiveBanks() {
        return ResponseEntity.ok(
                ApiResponse.ok("Banks retrieved", bankService.listActiveBanks()));
    }

    // GET /api/banks/all — admin sees all including inactive
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BankResponse>>> listAllBanks() {
        return ResponseEntity.ok(
                ApiResponse.ok("All banks retrieved", bankService.listAllBanks()));
    }

    // GET /api/banks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankResponse>> getBank(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Bank retrieved", bankService.getById(id)));
    }

    // POST /api/banks — admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BankResponse>> createBank(
            @Valid @RequestBody CreateBankRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bank created", bankService.create(request)));
    }

    // PUT /api/banks/{id} — admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BankResponse>> updateBank(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBankRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Bank updated", bankService.update(id, request)));
    }

    // DELETE /api/banks/{id} — soft delete, admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateBank(@PathVariable Long id) {
        bankService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("Bank deactivated"));
    }
}