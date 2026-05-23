package com.settleFlow.modules.banking.repository;

import com.settleFlow.modules.banking.enums.CreditStatus;
import com.settleFlow.modules.banking.model.CustomerCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerCreditRepository extends JpaRepository<CustomerCredit, Long> {

    List<CustomerCredit> findAllByCustomerId(Long customerId);

    Optional<CustomerCredit> findByCustomerIdAndBankId(Long customerId, Long bankId);

    List<CustomerCredit> findAllByCustomerIdAndStatus(Long customerId, CreditStatus status);

    boolean existsByCustomerIdAndBankId(Long customerId, Long bankId);

    @Query("SELECT cc FROM CustomerCredit cc WHERE cc.bank.id = :bankId AND cc.status = 'ACTIVE'")
    List<CustomerCredit> findActiveCreditsByBank(Long bankId);
}