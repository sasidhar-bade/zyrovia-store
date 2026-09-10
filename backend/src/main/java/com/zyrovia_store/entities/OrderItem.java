package com.zyrovia_store.entities;

import java.math.BigDecimal;

import com.zyrovia_store.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be greater than zero")
	@Column(nullable = false)
	private Integer quantity;

	@NotNull(message = "Price is required")
	@Digits(
			integer = 10,
			fraction = 2,
			message = "Price must contain at most 10 integer"
					  + " digits and 2 decimal places"
	)
	@Column(
			nullable = false,
			precision = 12,
			scale = 2
	)
	private BigDecimal price;
	
	@NotNull(message = "Order status is required")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@NotNull(message = "Order is required")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "order_id", 
			nullable = false
	)
	private Order order;

	@NotNull(message = "Product is required")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "product_id", 
			nullable = false
	)
	private Product product;
}