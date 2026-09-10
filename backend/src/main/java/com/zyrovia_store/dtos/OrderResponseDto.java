package com.zyrovia_store.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
		name = "Order Response",
		description = "Response containing complete order information"
)
public class OrderResponseDto {

	@Schema(
			description = "Unique ID of the order",
			example = "3"
	)
	private Long orderId;

	@Schema(
			description = "ID of the user who placed the order",
			example = "5"
	)
	private Long userId;

	@Schema(
			description = "Date and time when the order was placed",
			example = "2026-09-08T16:30:31"
	)
	private LocalDateTime orderDate;

	@Schema(
			description = "Total monetary value of the order",
			example = "1310.28"
	)
	private BigDecimal totalAmount;

	@Schema(
			description = "List of items contained in the order"
	)
	private List<OrderItemResponseDto> items;
}