package com.settleFlow.modules.merchant.dto.response;

import com.settleFlow.modules.merchant.enums.MerchantStatus;
import com.settleFlow.modules.merchant.model.Merchant;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class MerchantResponse {

    private Long id;
    private Long userId;
    private String storeName;
    private String description;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private MerchantStatus status;
    private LocalDateTime createdAt;

    public static MerchantResponse from(Merchant m) {
        return MerchantResponse.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .storeName(m.getStoreName())
                .description(m.getDescription())
                .logoUrl(m.getLogoUrl())
                .contactEmail(m.getContactEmail())
                .contactPhone(m.getContactPhone())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}