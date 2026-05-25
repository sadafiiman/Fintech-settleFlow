package com.settleFlow.modules.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterMerchantRequest {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Description is required")
    private String description;

    @Email
    private String contactEmail;

    private String contactPhone;

    private String logoUrl;
}