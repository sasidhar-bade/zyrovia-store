package com.zyrovia_store.security;

import org.springframework.stereotype.Component;

import com.zyrovia_store.repositories.CartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartSecurity {
	
	private final CartRepository cartRepository;
	
	public boolean isOwner(Long cartId, String email) {
		
		return this.cartRepository.findById(cartId)
				.map(cart -> cart.getUser().getEmail().equals(email))
				.orElse(false);
	}
}
