package com.settleFlow.modules.merchant.repository;

import com.settleFlow.modules.merchant.enums.ProductStatus;
import com.settleFlow.modules.merchant.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findAllByStatusAndCategory(ProductStatus status,
                                             String category,
                                             Pageable pageable);

    List<Product> findAllByMerchantIdAndStatusNot(Long merchantId, ProductStatus status);

    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty " +
            "WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(Long id, int qty);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :qty WHERE p.id = :id")
    void incrementStock(Long id, int qty);
}