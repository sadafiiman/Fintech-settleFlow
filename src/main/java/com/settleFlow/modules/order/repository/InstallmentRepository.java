package com.settleFlow.modules.order.repository;

import com.settleFlow.modules.order.enums.InstallmentStatus;
import com.settleFlow.modules.order.model.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findAllByOrderIdOrderByInstallmentNumberAsc(Long orderId);

    // Spring Data resolves "Order_CustomerId" as order.customerId — nested property path
    List<Installment> findAllByOrder_CustomerIdOrderByDueDateAsc(Long customerId);

    boolean existsByOrderIdAndStatus(Long orderId, InstallmentStatus status);

    // For the overdue-marking job (see InstallmentService.markOverdueInstallments)
    List<Installment> findAllByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);
}