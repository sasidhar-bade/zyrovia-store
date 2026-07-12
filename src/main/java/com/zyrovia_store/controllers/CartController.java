package com.zyrovia_store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.CartRequestDto;
import com.zyrovia_store.dtos.CartResponseDto;
import com.zyrovia_store.services.ICartServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final ICartServices cartServices;

	// Add product to cart
	@PostMapping
	public ResponseEntity<CartResponseDto> addToCartApiHandler(@RequestBody CartRequestDto requestDto) {

		CartResponseDto responseDto = this.cartServices.addToCart(requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// Get current user's cart
	@GetMapping
	public ResponseEntity<CartResponseDto> getCartApiHandler() {

		return ResponseEntity.ok(this.cartServices.getCart());
	}

	// Update cart item quantity
	@PatchMapping("/{cartItemId}")
	public ResponseEntity<CartResponseDto> updateQuantityApiHandler(@PathVariable Long cartItemId,
			@RequestParam Integer quantity) {

		return ResponseEntity.ok(this.cartServices.updateQuantity(cartItemId, quantity));
	}

	// Remove item from cart
	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<Void> removeFromCartApiHandler(@PathVariable Long cartItemId) {

		this.cartServices.removeFromCart(cartItemId);

		return ResponseEntity.noContent().build();
	}

	// Clear current user's cart
	@DeleteMapping("/clear")
	public ResponseEntity<Void> clearCartApiHandler() {

		this.cartServices.clearCart();

		return ResponseEntity.noContent().build();
	}
}
