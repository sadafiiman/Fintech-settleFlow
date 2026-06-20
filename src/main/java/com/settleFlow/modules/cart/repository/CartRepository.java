package com.settleFlow.modules.cart.repository;

import com.settleFlow.modules.cart.enums.CartStatus;
import com.settleFlow.modules.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerIdAndStatus(Long customerId, CartStatus status);
}