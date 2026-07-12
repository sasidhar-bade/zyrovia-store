package com.zyrovia_store.services;

import com.zyrovia_store.dtos.CartRequestDto;
import com.zyrovia_store.dtos.CartResponseDto;

public interface ICartServices {

	CartResponseDto addToCart(CartRequestDto cartRequestDto);

	CartResponseDto getCart();

	CartResponseDto updateQuantity(Long cartItemId, Integer quantity);

	void removeFromCart(Long cartItemId);

	void clearCart();
}
