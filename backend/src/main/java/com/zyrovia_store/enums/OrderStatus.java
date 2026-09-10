package com.zyrovia_store.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		description = """
					Status of an order item.
	
	                Valid transitions:
	                - PENDING -> CONFIRMED or CANCELLED
	                - CONFIRMED -> PROCESSING or CANCELLED
	                - PROCESSING -> SHIPPED
	                - SHIPPED -> DELIVERED
	
	                DELIVERED and CANCELLED are final statuses.
				"""
		)
public enum OrderStatus {

	@Schema(
				description = "Order item has been created and"
							  + " is waiting for confirmation"
			)
	PENDING,
	
	@Schema(
				description = "Order item has been confirmed by"
					      	  + " the seller or administrator"
			)
	CONFIRMED,
	
	@Schema(
				description = "Order item is currently being processed"
			)
	PROCESSING,
	
	@Schema(
			description = "Order item has been shipped"
		)
	SHIPPED,
	
	@Schema(
			description = "Order item has been successfully delivered"
		)
	DELIVERED,
	
	@Schema(
			description = "Order item has been cancelled"
		)
	CANCELLED
}