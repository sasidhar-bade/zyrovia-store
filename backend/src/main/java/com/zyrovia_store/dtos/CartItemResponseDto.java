package com.zyrovia_store.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {

	private Long cartItemId;

	private Long productId;

	private String productName;

	private Integer quantity;

	private BigDecimal price;

	private BigDecimal totalPrice;

}
