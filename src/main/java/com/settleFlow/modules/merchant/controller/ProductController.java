package com.settleFlow.modules.merchant.controller;

import com.settleFlow.modules.merchant.dto.request.CreateProductRequest;
import com.settleFlow.modules.merchant.dto.request.UpdateProductRequest;
import com.settleFlow.modules.merchant.dto.response.ProductResponse;
import com.settleFlow.modules.merchant.service.ProductService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.response.PageResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> marketplace(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    String category) {
        return ResponseEntity.ok(
                ApiResponse.ok("Products retrieved",
                        productService.listMarketplace(page, size, category)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Product retrieved", productService.getById(id)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> myProducts() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Your products", productService.getMyProducts(userId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created",
                        productService.create(userId, request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Product updated",
                        productService.update(userId, id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Product removed"));
    }
}