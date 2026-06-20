package com.settleFlow.modules.order.repository;

import com.settleFlow.modules.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
}