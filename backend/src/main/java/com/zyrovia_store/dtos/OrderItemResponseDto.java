package com.zyrovia_store.dtos;

import java.math.BigDecimal;

import com.zyrovia_store.enums.OrderStatus;

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
public class OrderItemResponseDto {

	private Long productId;

	private String productName;

	private Integer quantity;

	private BigDecimal price;

	private BigDecimal totalPrice;
	
	private OrderStatus orderStatus;
}