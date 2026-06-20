package com.settleFlow.modules.cart.service;

import com.settleFlow.modules.banking.model.Bank;
import com.settleFlow.modules.banking.service.BankService;
import com.settleFlow.modules.banking.service.CreditQueryService;
import com.settleFlow.modules.banking.service.CreditService;
import com.settleFlow.modules.cart.dto.request.SubmitCartRequest;
import com.settleFlow.modules.cart.dto.response.CartResponse;
import com.settleFlow.modules.cart.enums.CartStatus;
import com.settleFlow.modules.cart.model.Cart;
import com.settleFlow.modules.cart.model.CartItem;
import com.settleFlow.modules.cart.repository.CartRepository;
import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.merchant.service.ProductService;
import com.settleFlow.modules.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartCheckoutService {

    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductService productService;        // merchant domain
    private final BankService bankService;               // banking domain
    private final CreditQueryService creditQueryService;  // banking domain — read
    private final CreditService creditService;            // banking domain — write
    private final CustomerService customerService;         // customer domain

    /**
     * Validates everything, then atomically reserves stock and deducts credit.
     * On success the cart moves to APPROVED — the order domain will create the
     * actual Order + Installment records from this approved cart.
     *
     * @Transactional ensures: if ANY step fails (stock, credit, anything),
     * the whole operation rolls back — no partial reservation ever happens.
     */
    @Transactional
    public CartResponse submitCart(Long userId, SubmitCartRequest request) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = cartService.getOrCreateOpenCart(customer.getId());

        List<CartItem> items = cartService.getItems(cart.getId());
        if (items.isEmpty()) {
            throw BusinessException.badRequest("Cannot checkout an empty cart");
        }

        // 1. Validate bank and installment range
        Bank bank = bankService.findActiveOrThrow(request.getBankId());
        validateInstallmentMonths(bank, request.getInstallmentMonths());

        // 2. Calculate total
        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Check credit is sufficient BEFORE touching any stock
        if (!creditQueryService.hasSufficientCredit(customer.getId(), bank.getId(), total)) {
            BigDecimal available = creditQueryService.getAvailableCredit(
                    customer.getId(), bank.getId());
            throw BusinessException.badRequest(
                    String.format("Insufficient credit with %s. Available: %s, Required: %s",
                            bank.getName(), available, total));
        }

        // 4. Check stock is sufficient for every item BEFORE reserving any of them
        //    (avoids partially reserving stock then failing on item #3)
        for (CartItem item : items) {
            var product = productService.findActiveProductOrThrow(item.getProductId());
            if (!product.hasSufficientStock(item.getQuantity())) {
                throw BusinessException.badRequest(
                        "Insufficient stock for: " + product.getName());
            }
        }

        // 5. Reserve stock — atomic decrement per item (race-condition safe,
        //    see ProductRepository.decrementStock — uses WHERE stock >= qty)
        for (CartItem item : items) {
            productService.reserveStock(item.getProductId(), item.getQuantity());
        }

        // 6. Deduct credit — only after stock is successfully reserved
        creditService.deductCredit(customer.getId(), bank.getId(), total);

        // 7. Mark cart as approved and ready for order creation
        cart.setBankId(bank.getId());
        cart.setInstallmentMonths(request.getInstallmentMonths());
        cart.setStatus(CartStatus.APPROVED);
        cartRepository.save(cart);

        return CartResponse.from(cart, items);
    }

    private void validateInstallmentMonths(Bank bank, int months) {
        if (months < bank.getMinInstallmentMonths() || months > bank.getMaxInstallmentMonths()) {
            throw BusinessException.badRequest(
                    String.format("%s allows installments between %d and %d months",
                            bank.getName(), bank.getMinInstallmentMonths(), bank.getMaxInstallmentMonths()));
        }
    }
}