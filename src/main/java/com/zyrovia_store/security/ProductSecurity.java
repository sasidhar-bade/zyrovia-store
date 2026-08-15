package com.zyrovia_store.security;

import org.springframework.stereotype.Component;

import com.zyrovia_store.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductSecurity {
	
	private final ProductRepository productRepository;
	
	public boolean isOwner(Long productId, String email) {
		
		return this.productRepository.findById(productId)
				.map(product -> product.getSeller() != null 
											&& product.getSeller()
													  .getEmail()
													  .equals(email))
				.orElse(false);
	}
}
