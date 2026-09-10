package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.ErrorResponseDto;
import com.zyrovia_store.dtos.OrderResponseDto;
import com.zyrovia_store.enums.OrderStatus;
import com.zyrovia_store.services.IOrderServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(
	name = "Order Management",
	description = "APIs for placing orders, viewing orders,"
				  + " seller order management,"
				  + " and updating order item status"
)
public class OrderController {

	// Service layer dependency
	private final IOrderServices orderServices;
	
	// =============== Place an order By USER ===================

	@Operation(
			summary = "Place an order",
			description = """
						Creates a new order using the authenticated user's cart.
	
		                The cart must contain at least one item.
		
		                Product stock is validated before placing the order.
		
					    After the order is successfully created:
		                - Product stock is reduced.
		                - Order items are created with PENDING status.
		                - The user's cart is cleared.
		
		                Only USER role can access this endpoint.
					"""
	)
	@ApiResponses({
		@ApiResponse(
				responseCode = "201",
				description = "Order placed successfully",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "400",
				description = "Cart is empty, "
							  + "product is out of stock, "
							  + "or invalid request",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "401",
				description = "Authentication is required",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "403",
				description = "User is not authorized to access this endpoint",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "404",
				description = "User or cart not found",
				content = @Content(
		                schema = @Schema(
		                        implementation = ErrorResponseDto.class
		                )
		        )
		),
	})
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<OrderResponseDto> placeOrderApiHandler() {

		OrderResponseDto responseDto = this.orderServices.placeOrder();

		return new ResponseEntity<>(
					responseDto, 
					HttpStatus.CREATED
				);
	}

	// ================ Get my orders By Login USER =================
	
	@Operation(
			summary = "Get my orders",
			description = """
						Returns all orders belonging to the currently authenticated user.
	
	                    Only USER role can access this endpoint.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200", 
					description = "Orders retrieved successfully", 
					content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
			),
			@ApiResponse(
					responseCode = "401", 
					description = "Authentication is required", 
					content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
			),
			@ApiResponse(
					responseCode = "403", 
					description = "User is not authorized to access this endpoint", 
					content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
			),
			@ApiResponse(
					responseCode = "404", 
					description = "Authenticated user not found", 
					content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
			),
	})
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getMyOrdersApiHandler() {

		return ResponseEntity.ok(this.orderServices.getMyOrders());
	}

	// ============= Get order by ID By USER or ADMIN ==============
	
	@Operation(
			summary = "Get order by ID",
			description = """
						Returns the details of a specific order.
	
	                    ADMIN can access any order.
	
	                    USER can access only an order that belongs to them.
					"""
	)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "Orders retrieved successfully",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "401",
				description = "Authentication is required",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "403",
				description = "User is not authorized to access this endpoint",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "404",
				description = "Order not found",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
	})
	@PreAuthorize(
			"hasRole('ADMIN') or " +
			"(hasRole('USER') and " +
			"@orderSecurity.isOwner(#orderId,authentication.name))"
	)
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponseDto> getOrderById(
			@PathVariable 
			@Parameter(
					description = "Unique ID of the order",
					required = true,
					example = "3"
			)
			Long orderId) {

		return ResponseEntity.ok(this.orderServices.getOrderById(orderId));
	}
	
	// ================ Get all orders By ADMIN ===================
	
	@Operation(
			summary = "Get all orders",
			description = """
						Returns all orders available in the system.
	
	                    Only ADMIN role can access this endpoint.
					"""
	)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "All orders retrieved successfully",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "401",
				description = "Authentication is required",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		),
		@ApiResponse(
				responseCode = "403",
				description = "Only ADMIN can access this endpoint",
				content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
		)
	})
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/all")
	public ResponseEntity<List<OrderResponseDto>> getAllOrdersApiHandler(){
		
		return ResponseEntity.ok(this.orderServices.getAllOrders());
	}
	
	// =============== Get seller orders ===================
	
	@Operation(
			summary = "Get seller orders",
			description = """
					    Returns orders containing products that belong to
	                    the currently authenticated seller.
	
	                    A seller sees only order items associated with
	                    products owned by that seller.
	
	                    Only SELLER role can access this endpoint.
					"""
	)
	@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Seller orders retrieved successfully",
                content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Authentication is required",
                content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Only SELLER can access this endpoint",
                content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Seller not found",
                content = @Content(
						schema = @Schema(
								implementation = ErrorResponseDto.class
						)
				)
        )
})
	@PreAuthorize("hasRole('SELLER')")
	@GetMapping("/seller")
	public ResponseEntity<List<OrderResponseDto>> getSellerOrdersApiHandler(Authentication authentication){
		
		return ResponseEntity.ok(this.orderServices.getSellerOrders(authentication));
	}
	
	// ============ Update order item status BY ADMIN or SELLER Own Products ============
	
	@Operation(
	        summary = "Update order item status",
	        description = """
		                Updates the status of a specific order item.
	
		                ADMIN can update any order item.
		                SELLER can update only order items containing their own products.
	
		                Valid status transitions:
		                - PENDING -> CONFIRMED or CANCELLED,
		                - CONFIRMED -> PROCESSING or CANCELLED,
		                - PROCESSING -> SHIPPED,
		                - SHIPPED -> DELIVERED
	
		                DELIVERED and CANCELLED are final statuses.
	                """
	)
	@ApiResponses({
	        @ApiResponse(
	                responseCode = "200",
	                description = "Order item status updated successfully",
	                content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
	        ),
	        @ApiResponse(
	                responseCode = "400",
	                description = """
		                		Bad request. Possible reasons:
		                		- Missing status parameter
                             - Invalid status value
                             - Invalid status transition
                             - Order item does not belong to the specified order
	                		""",
	                	content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
	        ),
	        @ApiResponse(
	        			responseCode = "401",
	        			description = "Authentication is required",
	        			content = @Content(
								schema = @Schema(
										implementation = ErrorResponseDto.class
								)
						)
	        	),
	        @ApiResponse(
	                responseCode = "403",
	                description = """
	                		 	User is not authorized to update this order item.
                             A seller can update only their own product's order items.
	                		""",
	                	content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Order, order item or seller not found",
	                content = @Content(
							schema = @Schema(
									implementation = ErrorResponseDto.class
							)
					)
	        )
	})
	@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
	@PatchMapping("/{orderId}/items/{orderItemId}/status")
	public ResponseEntity<OrderResponseDto> updateOrderItemStatusApiHandler(
						@PathVariable 
						@Parameter(
								description = "Unique ID of the order",
								required = true,
								example = "3"
						)
						Long orderId,
						
						@PathVariable
						@Parameter(
								description = "Unique ID of the order item",
								required = true,
								example = "4"
						)
						Long orderItemId,
						
						@RequestParam 
						@Parameter(
								description = """ 
										
										New status of the order item.
	
			                             Allowed values:
			                             - PENDING,
			                             - CONFIRMED,
			                             - PROCESSING,
			                             - SHIPPED,
			                             - DELIVERED,
			                             - CANCELLED
		                              """,
								required = true,
								example = "CONFIRMED"
						)
						OrderStatus status,
						Authentication authentication){
		
		return ResponseEntity.ok(
				this.orderServices.updateOrderItemStatus(
							orderId,
							orderItemId, 
							status, 
							authentication
						)
				);
	}
}