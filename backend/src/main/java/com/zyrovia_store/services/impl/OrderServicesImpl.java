package com.zyrovia_store.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.zyrovia_store.exceptions.BadRequestException;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.CartRepository;
import com.zyrovia_store.repositories.OrderItemRepository;
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
	
	// Repository for Order Items database operations
	private final OrderItemRepository orderItemRepository;

	// Repository for Cart database operations
	private final CartRepository cartRepository;

	// Repository for Product database operations
	private final ProductRepository productRepository;

	// Repository for User database operations
	private final UserRepository userRepository;

	// Get currently logged-in user from JWT authentication
	private User getCurrentUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String email = authentication.getName();
		
		return this.userRepository.findByEmail(email)
				.orElseThrow(
							()-> new ResourceNotFoundException(
									"User not found with email : " + email)
						);
	}

	// Convert Order Entity into OrderResponseDto
	private OrderResponseDto mapToResponseDto(Order order) {

		List<OrderItemResponseDto> items = new ArrayList<>();

		// Convert each OrderItem into OrderItemResponseDto
		for (OrderItem item : order.getOrderItems()) {

			items.add(OrderItemResponseDto.builder()
					.orderItemId(item.getId())
					.productId(item.getProduct().getId())
					.productName(item.getProduct().getName())
					.quantity(item.getQuantity())
					.totalPrice(item.getPrice()
							.multiply(BigDecimal.valueOf(item.getQuantity())))
					.price(item.getPrice())
					.orderStatus(item.getStatus())
					.build());
		}

		// Build final order response
		return OrderResponseDto.builder()
				.orderId(order.getId())
				.userId(order.getUser().getId())
				.orderDate(order.getOrderDate())
				.totalAmount(order.getTotalAmount())
				.items(items)
				.build();
	}

	// Convert current user's cart into an order
	@Override
	public OrderResponseDto placeOrder() {

		User user = this.getCurrentUser();

		// Fetch user's cart
		Cart cart = this.cartRepository.findByUserId(user.getId())
				.orElseThrow(
							() -> new ResourceNotFoundException("Cart not found")
						);

		// Validate cart is not empty
		if (cart.getCartItems().isEmpty()) {

			throw new BadRequestException("Cart is empty");
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

				throw new BadRequestException(
							product.getName() 
							+ " is out of stock"
						);
			}

			// Reduce product stock after successful validation
			product.setStock(product.getStock() - cartItem.getQuantity());

			this.productRepository.save(product);

			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(product.getPrice());
			orderItem.setStatus(OrderStatus.PENDING);
			
			orderItems.add(orderItem);

			// Calculate order total amount
			totalAmount = totalAmount.add(
					product.getPrice()
					.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
		}

		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);

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

		return this.orderRepository
				.findByUser_Id(user.getId())
				.stream()
				.map(this::mapToResponseDto)
				.toList();
	}

	// Get order details by order id
	@Override
	public OrderResponseDto getOrderById(Long orderId) {

		User user = this.getCurrentUser();

		Order order = this.orderRepository.findById(orderId)
				.orElseThrow(
							() -> new ResourceNotFoundException("Order not found")
						);

		// Ensure order belongs to current user
		if (!order.getUser()
				  .getId()
				  .equals(user.getId())
				  ) {

			throw new AccessDeniedException(
						"You are not authorized to access this order"
					);
		}

		return this.mapToResponseDto(order);
	}

	@Override
	public List<OrderResponseDto> getAllOrders() {
		
		return this.orderRepository.findAll()
				.stream()
				.map(this::mapToResponseDto)
				.toList();
	}

	@Override
	public List<OrderResponseDto> getSellerOrders(Authentication authentication) {
		
		// 1. Get logged-in user's email from JWT
		String email = authentication.getName();
		
		// 2. Find logged-in seller
		User seller = this.userRepository.findByEmail(email)
				.orElseThrow(
							() -> new ResourceNotFoundException(
									"Seller not found with email : " + email)
						);
		
	    // 3. Find orders containing seller's products
		List<Order> orders = this.orderRepository.findOrdersBySellerId(seller.getId());

		// 4. Convert Entity -> DTO
		return orders.stream()
				.map(this::mapToResponseDto)
				.toList();
	}

	@Override
	public OrderResponseDto updateOrderItemStatus(
			Long orderId, 
			Long orderItemId, 
			OrderStatus status,
			Authentication authentication) {
		
		// 1.Validate status
		if(status == null) {
			
			throw new BadRequestException("Order status cannot be null");
		}
		
	    // 2. Check whether logged-in user is ADMIN
		boolean isAdmin = authentication.getAuthorities()
				.stream()
				.anyMatch(
						authority -> authority.getAuthority().equals("ROLE_ADMIN")
						);
		
		// 3. Find order
		Order order = this.orderRepository.findById(orderId)
				.orElseThrow(
							()-> new ResourceNotFoundException(
									"Order not found with id : " + orderId)
						);
		
		// 4. Find Order Item
		OrderItem orderItem = this.orderItemRepository.findById(orderItemId)
				.orElseThrow(
							()-> new ResourceNotFoundException(
									"Order item not found with id : " + orderItemId)
						);
		
		 // 5. Verify order item belongs to this order
		if(!orderItem.getOrder()
					 .getId()
					 .equals(order.getId())
					 ) {
			
			throw new BadRequestException(
						"Order item does not belong to this order"
					);
		}
		
		// 6. ADMIN can update any order item
		if(!isAdmin) {
			
			// 7. Get logged-in user's email from JWT
			String email = authentication.getName();
					
			// 8. Find logged-in seller
			User seller = this.userRepository.findByEmail(email)
							.orElseThrow(
										() -> new ResourceNotFoundException(
												"Seller not found with email : " + email)
									);
			
			// 9. Get product from order item
			Product product = orderItem.getProduct();
			
			// 10. Verify product belongs to logged-in seller
			if(product.getSeller() == null 
					|| !product.getSeller()
							   .getId()
							   .equals(seller.getId())
							   ) {
				
				throw new AccessDeniedException(
						"You are not authorized to update this order item");
			}
		}
		
		// 11. Validate status transition
		this.validateStatusTransition(orderItem.getStatus(), status);
		
		// 12. Update order status
		orderItem.setStatus(status);
		
		// 13. Save
		this.orderItemRepository.save(orderItem);
		
		// 14. Return and Convert Entity -> DTO
		return this.mapToResponseDto(order);
	}
	
	// Validate whether the requested status transition is allowed
	private void validateStatusTransition(
			OrderStatus currentStatus,
			OrderStatus newStatus) {
		
		// Current status should always exist
		if(currentStatus == null) {
			throw new BadRequestException(
						"Current order status cannot be null"
					);
		}
		
		// New status should always exist
		if(newStatus == null) {
			throw new BadRequestException(
						"New order status cannot be null"
					);
		}
		
		// Delivered is a final status
		if(currentStatus == OrderStatus.DELIVERED) {
			throw new BadRequestException(
						"Delivered order item cannot be updated"
					);
		}
		
		// Cancelled is a final status
		if(currentStatus == OrderStatus.CANCELLED) {
			throw new BadRequestException(
						"Cancelled order item cannot be updated"
					);
		}
		
		// Validate allowed status transitions
		switch (currentStatus) { 
		
		// PENDING can move to CONFIRMED or CANCELLED
		case PENDING: {
			
			if(newStatus != OrderStatus.CONFIRMED 
					&& newStatus != OrderStatus.CANCELLED) {
				
				throw new BadRequestException(
						"Invalid status transition from "
						+ currentStatus
						+ " to "
						+ newStatus);
			}
			break;
		}
		
		// CONFIRMED can move to PROCESSING or CANCELLED
		case CONFIRMED: {
			
			if(newStatus != OrderStatus.PROCESSING 
					&& newStatus != OrderStatus.CANCELLED) {
				
				throw new BadRequestException(
						"Invalid status transition from "
						+ currentStatus
						+ " to "
						+ newStatus);
			}
			break;
		}
		
		// PROCESSING can move only to SHIPPED
        case PROCESSING: {
        	
        		if(newStatus != OrderStatus.SHIPPED) {
				
				throw new BadRequestException(
						"Invalid status transition from "
						+ currentStatus
						+ " to "
						+ newStatus);
			}
			break;
		}
        
     // SHIPPED can move only to DELIVERED
		case SHIPPED: {
			
			if(newStatus != OrderStatus.DELIVERED) {
				
				throw new BadRequestException(
						"Invalid status transition from "
						+ currentStatus
						+ " to "
						+ newStatus);
			}
			break;
		}

		// Reject any undefined status transition
		default:
			throw new BadRequestException(
						"Invalid order status transition"
					);
		}
	}
}