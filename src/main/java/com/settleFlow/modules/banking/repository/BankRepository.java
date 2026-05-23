package com.settleFlow.modules.banking.repository;

import com.settleFlow.modules.banking.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    List<Bank> findAllByActiveTrue();

    Optional<Bank> findByIdAndActiveTrue(Long id);

    boolean existsByName(String name);
}