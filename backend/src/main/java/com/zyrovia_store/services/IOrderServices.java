package com.zyrovia_store.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.zyrovia_store.dtos.OrderResponseDto;
import com.zyrovia_store.enums.OrderStatus;

public interface IOrderServices {

	OrderResponseDto placeOrder();

	List<OrderResponseDto> getMyOrders();

	OrderResponseDto getOrderById(Long orderId);

	List<OrderResponseDto> getAllOrders();
	
	List<OrderResponseDto> getSellerOrders(Authentication authentication);
	
	OrderResponseDto updateOrderItemStatus(
			Long orderId, 
			Long orderItemId, 
			OrderStatus status,
			Authentication authentication);
}
