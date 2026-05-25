package com.settleFlow.modules.merchant.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMerchantRequest {

    private String storeName;
    private String description;

    @Email
    private String contactEmail;

    private String contactPhone;
    private String logoUrl;
}