package com.settleFlow.modules.customer.controller;

import com.settleFlow.modules.customer.dto.request.CreateCustomerProfileRequest;
import com.settleFlow.modules.customer.dto.request.UpdateCustomerProfileRequest;
import com.settleFlow.modules.customer.dto.response.CustomerProfileResponse;
import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> createProfile(
            @Valid @RequestBody CreateCustomerProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Profile created",
                        customerService.createProfile(userId, request)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Profile retrieved",
                        customerService.getMyProfile(userId)));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateCustomerProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Profile updated",
                        customerService.updateProfile(userId, request)));
    }
}