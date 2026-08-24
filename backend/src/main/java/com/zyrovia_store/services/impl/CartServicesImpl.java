package com.zyrovia_store.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
			BigDecimal totalPrice = cartItem
					.getProduct()
					.getPrice()
					.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			grandTotal = grandTotal.add(totalPrice);

			items.add(CartItemResponseDto.builder()
					.cartItemId(cartItem.getId())
					.productId(cartItem.getProduct().getId())
					.productName(cartItem.getProduct().getName())
					.price(cartItem.getProduct().getPrice())
					.quantity(cartItem.getQuantity())
					.totalPrice(totalPrice)
					.build());
		}

		// Build final cart response
		return CartResponseDto.builder()
									.cartId(cart.getId())
									.userId(cart.getUser().getId())
									.items(items)
									.grandTotal(grandTotal)
									.build();
	}

	// Get currently logged-in user
	private User getCurrentUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String email = authentication.getName();
		
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email : " + email));
	}

	// Add product to current user's cart
	@Override
	public CartResponseDto addToCart(CartRequestDto cartRequestDto) {

		// 1. Get currently logged-in user
		User user = this.getCurrentUser();
		
		//2. Validate quantity
		if(cartRequestDto.getQuantity() == null 
				&& cartRequestDto.getQuantity() <=0) {
			
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}

		// 3. Validate product existence
		Product product = this.productRepository.findById(cartRequestDto.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		
		// 4. Validate product stock
		if(product.getStock() <= 0) {
			
			throw new IllegalArgumentException("Product is out of stock");
		}

		// 5. Validate requested quantity against stock
		if(cartRequestDto.getQuantity() > product.getStock()) {
			
			throw new IllegalArgumentException("Insufficient stock available");
		}
		
		// 6. Find existing cart or create new cart
		Cart cart = this.cartRepository.findByUserId(user.getId())
				.orElseGet(() -> {

						Cart newcart = new Cart();
			
						newcart.setUser(user);
						newcart.setCartItems(new ArrayList<>());
			
						return this.cartRepository.save(newcart);
				});

		// 7. Check whether product already exists in cart
		Optional<CartItem> existingItem = cart.getCartItems()
													.stream()
													.filter(item -> item.getProduct()
																	    .getId().equals(product.getId()))
													.findFirst();

		if (existingItem.isPresent()) {

			// 8. Increase quantity if item already exists
			CartItem item = existingItem.get();

			Integer updatedQuantity = item.getQuantity() + cartRequestDto.getQuantity();

			// 9. Validate stock availability
			if (updatedQuantity > product.getStock()) {

				throw new IllegalArgumentException("Insufficient stock available");
			}

			item.setQuantity(updatedQuantity);

		} else {

			// 10. Create new cart item
			CartItem item = new CartItem();

			item.setCart(cart);
			item.setProduct(product);
			item.setQuantity(cartRequestDto.getQuantity());

			cart.getCartItems().add(item);
		}

		// 11. save cart
		Cart savedCart = this.cartRepository.save(cart);

		// 12. Return updated cart
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

			throw new AccessDeniedException("You are not authorized to access this cart item");
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

			throw new AccessDeniedException("You are not authorized to access this cart item");
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
