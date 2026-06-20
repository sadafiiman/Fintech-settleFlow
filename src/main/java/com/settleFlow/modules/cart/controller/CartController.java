package com.settleFlow.modules.cart.controller;

import com.settleFlow.modules.cart.dto.request.AddToCartRequest;
import com.settleFlow.modules.cart.dto.request.SubmitCartRequest;
import com.settleFlow.modules.cart.dto.request.UpdateCartItemRequest;
import com.settleFlow.modules.cart.dto.response.CartResponse;
import com.settleFlow.modules.cart.service.CartCheckoutService;
import com.settleFlow.modules.cart.service.CartService;
import com.settleFlow.modules.shared.response.ApiResponse;
import com.settleFlow.modules.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;
    private final CartCheckoutService cartCheckoutService;

    // GET /api/cart
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Cart retrieved", cartService.getMyCart(userId)));
    }

    // POST /api/cart/items
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Valid @RequestBody AddToCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Item added", cartService.addItem(userId, request)));
    }

    // PUT /api/cart/items/{itemId}
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Item updated",
                        cartService.updateItemQuantity(userId, itemId, request)));
    }

    // DELETE /api/cart/items/{itemId}
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Item removed", cartService.removeItem(userId, itemId)));
    }

    // DELETE /api/cart
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }

    // POST /api/cart/submit — checkout: validates credit + stock, reserves both
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<CartResponse>> submitCart(
            @Valid @RequestBody SubmitCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.ok("Cart submitted — credit and stock reserved",
                        cartCheckoutService.submitCart(userId, request)));
    }
}