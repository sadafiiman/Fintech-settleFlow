package com.settleFlow.modules.merchant.dto.request;

import com.settleFlow.modules.merchant.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class UpdateProductRequest {

    private String name;
    private String description;

    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @Min(0)
    private Integer stock;

    private String imageUrl;
    private String category;
    private ProductStatus status;
}