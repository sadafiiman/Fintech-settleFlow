package com.settleFlow.modules.merchant.model;

import com.settleFlow.modules.merchant.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_merchants")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String storeName;

    @Column(length = 1000)
    private String description;

    private String logoUrl;

    private String contactEmail;

    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}