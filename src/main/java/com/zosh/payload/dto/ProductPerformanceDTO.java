package com.zosh.payload.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPerformanceDTO {
    private String productName;
    private Double quantitySold;
    private double percentage; // 0–100
}
