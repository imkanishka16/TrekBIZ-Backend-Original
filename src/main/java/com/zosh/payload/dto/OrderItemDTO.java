package com.zosh.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

        private Long id;
        private Long productId;
        private Double quantity;
        private ProductDTO product;
        private Double price;

}
