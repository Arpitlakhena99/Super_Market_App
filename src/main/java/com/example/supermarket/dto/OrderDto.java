package com.example.supermarket.dto;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal totalAmount;

    @NotNull
    private Long customerId;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();
}
