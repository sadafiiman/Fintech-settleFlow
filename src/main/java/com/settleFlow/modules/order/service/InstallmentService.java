package com.settleFlow.modules.order.service;

import com.settleFlow.modules.banking.service.CreditService;
import com.settleFlow.modules.customer.service.CustomerService;
import com.settleFlow.modules.order.dto.request.PayInstallmentRequest;
import com.settleFlow.modules.order.dto.response.InstallmentResponse;
import com.settleFlow.modules.order.enums.InstallmentStatus;
import com.settleFlow.modules.order.enums.OrderStatus;
import com.settleFlow.modules.order.model.Installment;
import com.settleFlow.modules.order.model.Order;
import com.settleFlow.modules.order.repository.InstallmentRepository;
import com.settleFlow.modules.order.repository.OrderRepository;
import com.settleFlow.modules.shared.exception.BusinessException;
import com.settleFlow.modules.shared.exception.NotFoundException;
import com.settleFlow.modules.shared.util.InstallmentMath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallmentService {

    private final InstallmentRepository installmentRepository;
    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    // ── Called by OrderService right after an Order is created ─────────────
    @Transactional
    public List<Installment> generateSchedule(Order order, BigDecimal annualInterestRate) {
        BigDecimal monthlyPayment = InstallmentMath.monthlyPayment(
                order.getPrincipalAmount(), annualInterestRate, order.getInstallmentMonths());

        List<Installment> schedule = new ArrayList<>();
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= order.getInstallmentMonths(); i++) {
            Installment installment = Installment.builder()
                    .order(order)
                    .installmentNumber(i)
                    .dueDate(firstDueDate.plusMonths(i - 1))
                    .amount(monthlyPayment)
                    .paidAmount(BigDecimal.ZERO)
                    .status(InstallmentStatus.PENDING)
                    .build();
            schedule.add(installmentRepository.save(installment));
        }

        return schedule;
    }

    // ── Customer: view all installments across all orders ──────────────────
    public List<InstallmentResponse> getMyInstallments(Long userId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        return installmentRepository
                .findAllByOrder_CustomerIdOrderByDueDateAsc(customer.getId())
                .stream().map(InstallmentResponse::from).toList();
    }

    // ── Customer: view installments for one specific order ──────────────────
    public List<InstallmentResponse> getOrderInstallments(Long userId, Long orderId) {
        var customer = customerService.findByUserIdOrThrow(userId);
        Order order = orderRepository.findByIdAndCustomerId(orderId, customer.getId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        return installmentRepository.findAllByOrderIdOrderByInstallmentNumberAsc(order.getId())
                .stream().map(InstallmentResponse::from).toList();
    }

    // ── Pay an installment ────────────────────────────────────────────────
    // Note: actual money movement (wallet debit) is wired in once the wallet
    // domain exists. For now this records the payment and updates status.
    @Transactional
    public InstallmentResponse payInstallment(Long userId, Long installmentId,
                                              PayInstallmentRequest request) {
        var customer = customerService.findByUserIdOrThrow(userId);

        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new NotFoundException("Installment not found: " + installmentId));

        if (!installment.getOrder().getCustomerId().equals(customer.getId())) {
            throw BusinessException.badRequest("This installment does not belong to you");
        }

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw BusinessException.badRequest("Installment is already paid");
        }

        installment.setPaidAmount(installment.getAmount());
        installment.setPaidAt(LocalDateTime.now());
        installment.setStatus(InstallmentStatus.PAID);
        installmentRepository.save(installment);

        checkAndCompleteOrder(installment.getOrder());

        return InstallmentResponse.from(installment);
    }

    // ── If every installment on the order is PAID, mark the order COMPLETED ──
    private void checkAndCompleteOrder(Order order) {
        boolean hasUnpaid = installmentRepository.existsByOrderIdAndStatus(
                order.getId(), InstallmentStatus.PENDING)
                || installmentRepository.existsByOrderIdAndStatus(
                order.getId(), InstallmentStatus.OVERDUE);

        if (!hasUnpaid) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        }
    }

    // ── Run periodically (e.g. via @Scheduled) to flag overdue installments ──
    @Transactional
    public void markOverdueInstallments() {
        List<Installment> overdue = installmentRepository.findAllByStatusAndDueDateBefore(
                InstallmentStatus.PENDING, LocalDate.now());

        overdue.forEach(i -> i.setStatus(InstallmentStatus.OVERDUE));
        installmentRepository.saveAll(overdue);
    }

    public List<Installment> getOrderInstallmentEntities(Long orderId) {
        return installmentRepository.findAllByOrderIdOrderByInstallmentNumberAsc(orderId);
    }
}