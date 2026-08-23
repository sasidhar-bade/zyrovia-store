package com.zyrovia_store.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

	private Long orderId;

	private Long userId;

	private LocalDateTime orderDate;

	private BigDecimal totalAmount;

	private List<OrderItemResponseDto> items;
}