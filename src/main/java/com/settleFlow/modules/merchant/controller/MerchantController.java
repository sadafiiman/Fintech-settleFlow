package com.settleFlow.modules.merchant.controller;

import com.settleFlow.modules.merchant.dto.request.RegisterMerchantRequest;
import com.settleFlow.modules.merchant.dto.request.UpdateMerchantRequest;
import com.settleFlow.modules.merchant.dto.response.MerchantResponse;
import com.settleFlow.modules.merchant.service.MerchantService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<MerchantResponse>> register(
            @Valid @RequestBody RegisterMerchantRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Store registered, pending approval",
                        merchantService.register(userId, request)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<MerchantResponse>> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Profile retrieved",
                        merchantService.getMyProfile(userId)));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateProfile(
            @Valid @RequestBody UpdateMerchantRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Profile updated",
                        merchantService.update(userId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> getMerchant(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Merchant retrieved", merchantService.getById(id)));
    }
}