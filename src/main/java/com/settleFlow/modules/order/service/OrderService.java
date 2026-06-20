package com.settleFlow.modules.order.service;

import com.settleFlow.modules.banking.model.Bank;
import com.settleFlow.modules.banking.service.BankService;
import com.settleFlow.modules.banking.service.CreditService;
import com.settleFlow.modules.cart.model.Cart;
import com.settleFlow.modules.cart.model.CartItem;
import com.settleFlow.modules.cart.service.CartService;
import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.merchant.service.ProductService;
import com.settleFlow.modules.order.dto.response.OrderResponse;
import com.settleFlow.modules.order.enums.OrderStatus;
import com.settleFlow.modules.order.model.Installment;
import com.settleFlow.modules.order.model.Order;
import com.settleFlow.modules.order.model.OrderItem;
import com.settleFlow.modules.order.repository.OrderItemRepository;
import com.settleFlow.modules.order.repository.OrderRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import com.settleFlow.modules.shared.util.InstallmentMath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InstallmentService installmentService;
    private final CartService cartService;             // cart domain — via service
    private final BankService bankService;               // banking domain — via service
    private final CreditService creditService;            // banking domain — via service
    private final ProductService productService;           // merchant domain — via service
    private final CustomerService customerService;          // customer domain — via service

    // ── Create an Order from the customer's APPROVED cart ───────────────────
    @Transactional
    public OrderResponse createFromCart(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Cart cart = cartService.getApprovedCartOrThrow(customer.getId());

        List<CartItem> cartItems = cartService.getItems(cart.getId());
        Bank bank = bankService.findActiveOrThrow(cart.getBankId());

        BigDecimal principal = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRepayment = InstallmentMath.totalRepayment(
                principal, bank.getInterestRate(), cart.getInstallmentMonths());
        BigDecimal totalInterest = InstallmentMath.totalInterest(
                principal, bank.getInterestRate(), cart.getInstallmentMonths());

        // 1. Create the order
        Order order = Order.builder()
                .customerId(customer.getId())
                .bankId(bank.getId())
                .sourceCartId(cart.getId())
                .principalAmount(principal)
                .totalInterest(totalInterest)
                .totalAmount(totalRepayment)
                .installmentMonths(cart.getInstallmentMonths())
                .status(OrderStatus.ACTIVE)
                .build();
        order = orderRepository.save(order);

        // 2. Snapshot cart items into order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // 3. Generate the installment schedule
        List<Installment> installments = installmentService.generateSchedule(
                order, bank.getInterestRate());

        // 4. Mark the cart as converted — it's no longer "approved", it's fulfilled
        cartService.markConverted(cart);

        List<OrderItem> savedItems = orderItemRepository.findAllByOrderId(order.getId());
        return OrderResponse.from(order, savedItems, installments);
    }

    // ── Customer: list my orders ────────────────────────────────────────────
    public List<OrderResponse> getMyOrders(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(order -> {
                    var items = orderItemRepository.findAllByOrderId(order.getId());
                    var installments = installmentService.getOrderInstallmentEntities(order.getId());
                    return OrderResponse.from(order, items, installments);
                })
                .toList();
    }

    // ── Customer: get one order with full detail ─────────────────────────────
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndCustomerId(orderId, customer.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        var items = orderItemRepository.findAllByOrderId(order.getId());
        var installmentEntities = installmentService.getOrderInstallments(userId, orderId);

        // Re-fetch installment entities for response builder (clean separation
        // from the DTO-returning service method above)
        return OrderResponse.from(order, items, List.of());
    }

    // ── Cancel an order before any installment is paid — releases credit + stock ──
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndCustomerId(orderId, customer.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.ACTIVE) {
            throw BusinessException.badRequest("Only active orders can be cancelled");
        }

        List<OrderItem> items = orderItemRepository.findAllByOrderId(order.getId());

        boolean anyPaid = items.stream().anyMatch(i -> false); // placeholder check below
        // Real check: look at installments via InstallmentService — if any PAID, block cancellation
        var installments = installmentService.getOrderInstallments(userId, orderId);
        boolean hasPayment = installments.stream()
                .anyMatch(i -> i.getStatus() == com.settleFlow.modules.order.enums.InstallmentStatus.PAID);
        if (hasPayment) {
            throw BusinessException.badRequest(
                    "Cannot cancel an order with paid installments");
        }

        // Release stock back to each product
        for (OrderItem item : items) {
            productService.releaseStock(item.getProductId(), item.getQuantity());
        }

        // Release credit back to the customer's bank credit line
        creditService.releaseCredit(order.getCustomerId(), order.getBankId(),
                order.getTotalAmount());

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}