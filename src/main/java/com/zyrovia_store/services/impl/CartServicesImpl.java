package com.zyrovia_store.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.CartItemResponseDto;
import com.zyrovia_store.dtos.CartRequestDto;
import com.zyrovia_store.dtos.CartResponseDto;
import com.zyrovia_store.entities.Cart;
import com.zyrovia_store.entities.CartItem;
import com.zyrovia_store.entities.Product;
import com.zyrovia_store.entities.User;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.CartItemRepository;
import com.zyrovia_store.repositories.CartRepository;
import com.zyrovia_store.repositories.ProductRepository;
import com.zyrovia_store.repositories.UserRepository;
import com.zyrovia_store.services.ICartServices;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServicesImpl implements ICartServices {

	// Repository for Cart database operations
	private final CartRepository cartRepository;

	// Repository for CartItem database operations
	private final CartItemRepository cartItemRepository;

	// Repository for Product database operations
	private final ProductRepository productRepository;

	// Repository for User database operations
	private final UserRepository userRepository;

	// Convert Cart Entity into CartResponseDto
	private CartResponseDto mapToResponseDto(Cart cart) {

		List<CartItemResponseDto> items = new ArrayList<>();

		BigDecimal grandTotal = BigDecimal.ZERO;

		// Convert each CartItem into CartItemResponseDto
		for (CartItem cartItem : cart.getCartItems()) {

			// Calculate total price for each cart item
			BigDecimal totalPrice = cartItem.getProduct().getPrice()
					.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			grandTotal = grandTotal.add(totalPrice);

			items.add(
					CartItemResponseDto.builder().cartItemId(cartItem.getId()).productId(cartItem.getProduct().getId())
							.productName(cartItem.getProduct().getName()).price(cartItem.getProduct().getPrice())
							.quantity(cartItem.getQuantity()).totalPrice(totalPrice).build());
		}

		// Build final cart response
		return CartResponseDto.builder().cartId(cart.getId()).userId(cart.getUser().getId()).items(items)
				.grandTotal(grandTotal).build();
	}

	// Get currently logged-in user
	// Temporary implementation using hardcoded user id
	// Later replace with JWT Authentication
	private User getCurrentUser() {

		Long userId = 1l; // Temporary

		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return user;
	}

	// Add product to current user's cart
	@Override
	public CartResponseDto addToCart(CartRequestDto cartRequestDto) {

		User user = this.getCurrentUser();

		// Validate product existence
		Product product = this.productRepository.findById(cartRequestDto.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

		// Find existing cart or create new cart
		Cart cart = this.cartRepository.findByUserId(user.getId()).orElseGet(() -> {

			Cart newcart = new Cart();

			newcart.setUser(user);

			newcart.setCartItems(new ArrayList<>());

			return this.cartRepository.save(newcart);
		});

		// Check whether product already exists in cart
		Optional<CartItem> existingItem = cart.getCartItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId())).findFirst();

		if (existingItem.isPresent()) {

			// Increase quantity if item already exists
			CartItem item = existingItem.get();

			Integer updatedQuantity = item.getQuantity() + cartRequestDto.getQuantity();

			// Validate stock availability
			if (updatedQuantity > product.getStock()) {

				throw new IllegalArgumentException("Insufficient stock available");
			}

			item.setQuantity(updatedQuantity);

		} else {

			// Create new cart item
			CartItem item = new CartItem();

			item.setCart(cart);
			item.setProduct(product);

			// Validate quantity
			if (cartRequestDto.getQuantity() <= 0) {

				throw new IllegalArgumentException("Quantity must be greater than zero");
			}

			// Validate stock
			if (product.getStock() < cartRequestDto.getQuantity()) {

				throw new IllegalArgumentException("Insufficient stock available");
			}

			item.setQuantity(cartRequestDto.getQuantity());

			cart.getCartItems().add(item);
		}

		Cart savedCart = this.cartRepository.save(cart);

		return mapToResponseDto(savedCart);
	}

	// Get current user's cart
	@Override
	public CartResponseDto getCart() {

		User user = this.getCurrentUser();

		// Validate cart existence
		Cart cart = this.cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

		return mapToResponseDto(cart);
	}

	// Update quantity of a cart item
	@Override
	public CartResponseDto updateQuantity(Long cartItemId, Integer quantity) {

		User user = this.getCurrentUser();

		// Validate cart item existence
		CartItem cartItem = this.cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

		// Ensure item belongs to current user
		if (!cartItem.getCart().getUser().getId().equals(user.getId())) {

			throw new IllegalStateException("Unauthorized access"); // later Custom Exception
		}

		// Validate quantity
		if (quantity <= 0) {

			throw new IllegalArgumentException("Quantity must be greater than zero");
		}

		// Validate stock availability
		if (cartItem.getProduct().getStock() < quantity) {

			throw new IllegalArgumentException("Insufficient stock available");
		}

		cartItem.setQuantity(quantity);

		CartItem updatedCartItem = this.cartItemRepository.save(cartItem);

		return mapToResponseDto(updatedCartItem.getCart());
	}

	// Remove a specific item from current user's cart
	@Override
	public void removeFromCart(Long cartItemId) {

		User user = this.getCurrentUser();

		// Validate cart item existence
		CartItem cartItem = this.cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

		// Ensure item belongs to current user
		if (!cartItem.getCart().getUser().getId().equals(user.getId())) {

			throw new IllegalStateException("Unauthorized access"); // later Custom Exception
		}

		this.cartItemRepository.delete(cartItem);
	}

	// Remove all items from current user's cart
	@Override
	public void clearCart() {

		User user = this.getCurrentUser();

		// Validate cart existence
		Cart cart = this.cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

		cart.getCartItems().clear();

		this.cartRepository.save(cart);
	}
}
