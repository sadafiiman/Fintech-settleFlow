package com.settleFlow.modules.wallet.repository;

import com.settleFlow.modules.wallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findAllByWalletIdOrderByCreatedAtDesc(Long walletId);

    // Balance = sum of all CREDITs minus sum of all DEBITs.
    // This single query is the entire "balance" of the wallet — nothing is stored.
    @Query("""
        SELECT COALESCE(SUM(
            CASE WHEN t.direction = 'CREDIT' THEN t.amount ELSE -t.amount END
        ), 0)
        FROM WalletTransaction t
        WHERE t.wallet.id = :walletId
        """)
    BigDecimal calculateBalance(Long walletId);
}