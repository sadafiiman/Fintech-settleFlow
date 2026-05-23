package com.settleFlow.modules.banking.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "banking_banks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal defaultCreditLimit;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer maxInstallmentMonths;

    @Column(nullable = false)
    private Integer minInstallmentMonths;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}