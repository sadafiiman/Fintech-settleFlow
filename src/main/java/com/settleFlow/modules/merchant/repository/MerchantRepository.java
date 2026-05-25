package com.settleFlow.modules.merchant.repository;

import com.settleFlow.modules.merchant.enums.MerchantStatus;
import com.settleFlow.modules.merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByStoreName(String storeName);

    List<Merchant> findAllByStatus(MerchantStatus status);

    Optional<Merchant> findByIdAndStatus(Long id, MerchantStatus status);
}