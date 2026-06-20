package com.zyrovia_store.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.OrderItemResponseDto;
import com.zyrovia_store.dtos.OrderResponseDto;
import com.zyrovia_store.entities.Cart;
import com.zyrovia_store.entities.CartItem;
import com.zyrovia_store.entities.Order;
import com.zyrovia_store.entities.OrderItem;
import com.zyrovia_store.entities.Product;
import com.zyrovia_store.entities.User;
import com.zyrovia_store.enums.OrderStatus;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.CartRepository;
import com.zyrovia_store.repositories.OrderRepository;
import com.zyrovia_store.repositories.ProductRepository;
import com.zyrovia_store.repositories.UserRepository;
import com.zyrovia_store.services.IOrderServices;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServicesImpl implements IOrderServices {

	// Repository for Order database operations
	private final OrderRepository orderRepository;

	// Repository for Cart database operations
	private final CartRepository cartRepository;

	// Repository for Product database operations
	private final ProductRepository productRepository;

	// Repository for User database operations
	private final UserRepository userRepository;

	// Get currently logged-in user
	// Temporary implementation using hard coded user id
	// Later replace with JWT Authentication
	private User getCurrentUser() {

		Long userId = 1l;

		return this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	// Convert Order Entity into OrderResponseDto
	private OrderResponseDto mapToResponseDto(Order order) {

		List<OrderItemResponseDto> items = new ArrayList<>();

		// Convert each OrderItem into OrderItemResponseDto
		for (OrderItem item : order.getOrderItems()) {

			items.add(OrderItemResponseDto.builder().productId(item.getProduct().getId())
					.productName(item.getProduct().getName()).quantity(item.getQuantity())
					.totalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).price(item.getPrice())
					.build());
		}

		// Build final order response
		return OrderResponseDto.builder().orderId(order.getId()).userId(order.getUser().getId())
				.orderDate(order.getOrderDate()).status(order.getStatus()).totalAmount(order.getTotalAmount())
				.items(items).build();
	}

	// Convert current user's cart into an order
	@Override
	public OrderResponseDto placeOrder() {

		User user = this.getCurrentUser();

		// Fetch user's cart
		Cart cart = this.cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

		// Validate cart is not empty
		if (cart.getCartItems().isEmpty()) {

			throw new IllegalArgumentException("Cart is empty");
		}

		List<OrderItem> orderItems = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		Order order = new Order();

		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		// Convert cart items into order items
		for (CartItem cartItem : cart.getCartItems()) {

			Product product = cartItem.getProduct();

			// Validate stock availability
			if (product.getStock() < cartItem.getQuantity()) {

				throw new IllegalArgumentException(product.getName() + " is out of stock");
			}

			// Reduce product stock after successful validation
			product.setStock(product.getStock() - cartItem.getQuantity());

			this.productRepository.save(product);

			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(product.getPrice());

			orderItems.add(orderItem);

			// Calculate order total amount
			totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
		}

		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);
		order.setStatus(OrderStatus.PENDING);

		// Clear cart after successful order placement
		Order savedOrder = this.orderRepository.save(order);

		cart.getCartItems().clear();

		this.cartRepository.save(cart);

		return this.mapToResponseDto(savedOrder);
	}

	// Get all orders of current user
	@Override
	public List<OrderResponseDto> getMyOrders() {

		User user = this.getCurrentUser();

		return this.orderRepository.findByUser_Id(user.getId()).stream().map(this::mapToResponseDto).toList();
	}

	// Get order details by order id
	@Override
	public OrderResponseDto getOrderById(Long orderId) {

		User user = this.getCurrentUser();

		Order order = this.orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		// Ensure order belongs to current user
		if (!order.getUser().getId().equals(user.getId())) {

			throw new IllegalArgumentException("Unauthorized access");
		}

		return this.mapToResponseDto(order);
	}

}
