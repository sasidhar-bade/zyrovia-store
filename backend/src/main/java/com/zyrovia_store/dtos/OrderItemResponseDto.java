package com.zyrovia_store.dtos;

import java.math.BigDecimal;

import com.zyrovia_store.enums.OrderStatus;

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
		name = "Order Item Response",
		description = "Information about a product contained in an order"
)
public class OrderItemResponseDto {
	
	@Schema(
	        description = "Unique ID of the order item",
	        example = "4"
	)
	private Long orderItemId;

	@Schema(
			description = "Unique ID of the product",
			example = "4"
	)
	private Long productId;

	@Schema(
			description = "Name of the ordered product",
			example = "Dining Chair"
	)
	private String productName;

	@Schema(
			description = "Quantity of the product ordered",
			example = "2"
	)
	private Integer quantity;

	@Schema(
			description = "Price of one unit of the product",
            example = "464.54"
	)
	private BigDecimal price;

	@Schema(
			description = "Total price for this order item",
            example = "929.08"
	)
	private BigDecimal totalPrice;
	
	@Schema(
			description = "Current fulfillment status of the order item",
            example = "PENDING"
	)
	private OrderStatus orderStatus;
}