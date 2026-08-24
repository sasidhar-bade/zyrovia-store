package com.zyrovia_store.security;

import org.springframework.stereotype.Component;

import com.zyrovia_store.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderSecurity {
	
	private final OrderRepository orderRepository;
	
	public boolean isOwner(Long orderId, String email) {
		
		return this.orderRepository.findById(orderId)
				.map(order -> order.getUser().getEmail().equals(email))
				.orElse(false);
	}
}