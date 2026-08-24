package com.zyrovia_store.dtos;

import java.math.BigDecimal;
import java.util.List;

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
public class CartResponseDto {

	private Long cartId;

	private Long userId;

	private List<CartItemResponseDto> items;

	private BigDecimal grandTotal;

}
