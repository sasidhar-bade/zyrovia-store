package com.zyrovia_store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	// USER can add product to own cart
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<CartResponseDto> addToCartApiHandler(@RequestBody CartRequestDto requestDto) {

		CartResponseDto responseDto = this.cartServices.addToCart(requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

    // USER can access only their own cart
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<CartResponseDto> getCartApiHandler() {

		return ResponseEntity.ok(this.cartServices.getCart());
	}

    // USER can update only their own cart
	@PreAuthorize("hasRole('USER')")
	@PatchMapping("/{cartItemId}")
	public ResponseEntity<CartResponseDto> updateQuantityApiHandler(@PathVariable Long cartItemId,
			@RequestParam Integer quantity) {

		return ResponseEntity.ok(this.cartServices.updateQuantity(cartItemId, quantity));
	}

    // USER can remove item from own cart
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<Void> removeFromCartApiHandler(@PathVariable Long cartItemId) {

		this.cartServices.removeFromCart(cartItemId);

		return ResponseEntity.noContent().build();
	}

    // USER can clear own cart
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/clear")
	public ResponseEntity<Void> clearCartApiHandler() {

		this.cartServices.clearCart();

		return ResponseEntity.noContent().build();
	}
}
