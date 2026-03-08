package com.example.supermarket.dto;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal unitPrice;
}
