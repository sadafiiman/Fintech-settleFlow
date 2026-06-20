package com.settleFlow.modules.cart.dto.response;

import com.settleFlow.modules.cart.enums.CartStatus;
import com.settleFlow.modules.cart.model.Cart;
import com.settleFlow.modules.cart.model.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class CartResponse {

    private Long id;
    private CartStatus status;
    private Long bankId;
    private Integer installmentMonths;
    private List<CartItemResponse> items;
    private Integer itemCount;
    private BigDecimal total;

    public static CartResponse from(Cart cart, List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .status(cart.getStatus())
                .bankId(cart.getBankId())
                .installmentMonths(cart.getInstallmentMonths())
                .items(items.stream().map(CartItemResponse::from).toList())
                .itemCount(items.size())
                .total(total)
                .build();
    }
}