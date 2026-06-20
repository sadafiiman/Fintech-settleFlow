package com.settleFlow.modules.order.controller;

import com.settleFlow.modules.order.dto.request.PayInstallmentRequest;
import com.settleFlow.modules.order.dto.response.InstallmentResponse;
import com.settleFlow.modules.order.service.InstallmentService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class InstallmentController {

    private final InstallmentService installmentService;

    // GET /api/installments — all installments across all my orders
    @GetMapping
    public ResponseEntity<ApiResponse<List<InstallmentResponse>>> myInstallments() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Installments retrieved",
                        installmentService.getMyInstallments(userId)));
    }

    // GET /api/installments/order/{orderId}
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<InstallmentResponse>>> orderInstallments(
            @PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Installments retrieved",
                        installmentService.getOrderInstallments(userId, orderId)));
    }

    // POST /api/installments/{id}/pay
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<InstallmentResponse>> payInstallment(
            @PathVariable Long id,
            @Valid @RequestBody PayInstallmentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Installment paid",
                        installmentService.payInstallment(userId, id, request)));
    }
}