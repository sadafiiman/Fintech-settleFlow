package com.settleFlow.modules.merchant.dto.response;

import com.settleFlow.modules.merchant.enums.ProductStatus;
import com.settleFlow.modules.merchant.model.Product;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
public class ProductResponse {

    private Long id;
    private Long merchantId;
    private String merchantStoreName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String category;
    private ProductStatus status;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .merchantId(p.getMerchant().getId())
                .merchantStoreName(p.getMerchant().getStoreName())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .category(p.getCategory())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}