package com.settleFlow.modules.cart.service;

import com.settleFlow.modules.cart.dto.request.AddToCartRequest;
import com.settleFlow.modules.cart.dto.request.UpdateCartItemRequest;
import com.settleFlow.modules.cart.dto.response.CartResponse;
import com.settleFlow.modules.cart.enums.CartStatus;
import com.settleFlow.modules.cart.model.Cart;
import com.settleFlow.modules.cart.model.CartItem;
import com.settleFlow.modules.cart.repository.CartItemRepository;
import com.settleFlow.modules.cart.repository.CartRepository;
import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.merchant.model.Product;
import com.settleFlow.modules.merchant.service.ProductService;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;     // merchant domain — via service
    private final CustomerService customerService;    // customer domain — via service

    // ── Get (or create) the customer's single OPEN cart ─────────────────────
    @Transactional
    public Cart getOrCreateOpenCart(Long customerId) {
        return cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.OPEN)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .customerId(customerId)
                                .status(CartStatus.OPEN)
                                .build()
                ));
    }

    // ── View current cart ────────────────────────────────────────────────────
    public CartResponse getMyCart(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = getOrCreateOpenCart(customer.getId());
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        return CartResponse.from(cart, items);
    }

    // ── Add item ──────────────────────────────────────────────────────────────
    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = getOrCreateOpenCart(customer.getId());

        // Validates product exists, is active — throws NotFoundException otherwise
        Product product = productService.findActiveProductOrThrow(request.getProductId());

        if (!product.hasSufficientStock(request.getQuantity())) {
            throw BusinessException.badRequest(
                    "Insufficient stock. Available: " + product.getStock());
        }

        // If product already in cart, increase quantity instead of duplicating
        var existing = cartItemRepository.findByCartIdAndProductId(
                cart.getId(), product.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + request.getQuantity();

            if (!product.hasSufficientStock(newQty)) {
                throw BusinessException.badRequest(
                        "Insufficient stock for total quantity. Available: " + product.getStock());
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())     // snapshot price now
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(item);
        }

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        return CartResponse.from(cart, items);
    }

    // ── Update quantity ───────────────────────────────────────────────────────
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId,
                                           UpdateCartItemRequest request) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = getOrCreateOpenCart(customer.getId());

        CartItem item = findOwnedItemOrThrow(cart, itemId);

        Product product = productService.findActiveProductOrThrow(item.getProductId());
        if (!product.hasSufficientStock(request.getQuantity())) {
            throw BusinessException.badRequest(
                    "Insufficient stock. Available: " + product.getStock());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        return CartResponse.from(cart, items);
    }

    // ── Remove item ────────────────────────────────────────────────────────────
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = getOrCreateOpenCart(customer.getId());

        CartItem item = findOwnedItemOrThrow(cart, itemId);
        cartItemRepository.delete(item);

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());
        return CartResponse.from(cart, items);
    }

    // ── Clear entire cart ────────────────────────────────────────────────────
    @Transactional
    public void clearCart(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = getOrCreateOpenCart(customer.getId());
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    // ── Internal — used by CartCheckoutService ──────────────────────────────
    public List<CartItem> getItems(Long cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }

    private CartItem findOwnedItemOrThrow(Cart cart, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw BusinessException.badRequest("This item does not belong to your cart");
        }
        return item;
    }


    // Used by OrderService — finds the cart that was just approved at checkout
    public Cart getApprovedCartOrThrow(Long customerId) {
        return cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException(
                        "No approved cart found. Please complete checkout first."));
    }

    // Used by OrderService — marks the cart as consumed once the Order is created
    @Transactional
    public void markConverted(Cart cart) {
        cart.setStatus(CartStatus.CONVERTED);
        cartRepository.save(cart);
    }
}