package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.OrderResponseDto;
import com.zyrovia_store.services.IOrderServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	// Service layer dependency
	private final IOrderServices orderServices;

	// Place order using current user's cart
	@PostMapping
	public ResponseEntity<OrderResponseDto> placeOrderApiHandler() {

		OrderResponseDto responseDto = this.orderServices.placeOrder();

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// Get all orders of current user
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getMyOrdersApiHandler() {

		return ResponseEntity.ok(this.orderServices.getMyOrders());
	}

	// Get specific order details by order id
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {

		return ResponseEntity.ok(this.orderServices.getOrderById(orderId));
	}
}
