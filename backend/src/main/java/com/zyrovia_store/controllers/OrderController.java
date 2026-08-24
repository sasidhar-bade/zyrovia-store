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

import com.zyrovia_store.dtos.OrderResponseDto;
import com.zyrovia_store.enums.OrderStatus;
import com.zyrovia_store.services.IOrderServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	// Service layer dependency
	private final IOrderServices orderServices;
	
	// =========================== USER ============================

	// Place order using current user's cart
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<OrderResponseDto> placeOrderApiHandler() {

		OrderResponseDto responseDto = this.orderServices.placeOrder();

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// USER can access their own orders
    // ADMIN can access can view orders
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getMyOrdersApiHandler() {

		return ResponseEntity.ok(this.orderServices.getMyOrders());
	}

	// ADMIN can access any order
    // USER can access own order
	@PreAuthorize(
			"hasRole('ADMIN') or " +
			"(hasRole('USER') and " +
			"@orderSecurity.isOwner(#orderId,authentication.name))"
	)
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {

		return ResponseEntity.ok(this.orderServices.getOrderById(orderId));
	}
	
	// ====================== ADMIN =============================
	
	// ADMIN can access all orders
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/all")
	public ResponseEntity<List<OrderResponseDto>> getAllOrdersApiHandler(){
		
		return ResponseEntity.ok(this.orderServices.getAllOrders());
	}
	
	// ====================== SELLER =============================
	
	// ADMIN and SELLER can access order
    // Seller sees orders containing their products
	@PreAuthorize("hasRole('SELLER')")
	@GetMapping("/seller")
	public ResponseEntity<List<OrderResponseDto>> getSellerOrdersApiHandler(Authentication authentication){
		
		return ResponseEntity.ok(this.orderServices.getSellerOrders(authentication));
	}
	
	// ADMIN and SELLER can access update order status
    // Seller updates fulfillment of their own order item
	@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
	@PatchMapping("/{orderId}/items/{orderItemId}/status")
	public ResponseEntity<OrderResponseDto> updateOrderItemStatusApiHandler(
						@PathVariable Long orderId,
						@PathVariable Long orderItemId,
						@RequestParam OrderStatus status,
						Authentication authentication){
		
		return ResponseEntity.ok(
				this.orderServices.updateOrderItemStatus(
						orderId,
						orderItemId, 
						status, 
						authentication)
				);
	}
}