package com.zyrovia_store.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Total amount is required")
	@Digits(
			integer = 10,
			fraction = 2,
			message = "Total amount must contain at most 10 integer"
					  + " digits and 2 decimal places"
	)
	@DecimalMin(
			value = "0.01",
			message = "Total amount must be greater than zero"
	)
	@Column(
			nullable = false,
			precision = 12,
			scale = 2
	)
	private BigDecimal totalAmount;

	@NotNull(message = "Order date is required")
	@Column(nullable = false)
	private LocalDateTime orderDate;

	@NotNull(message = "User is required")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "user_id", 
			nullable = false
	)
	private User user;

	@NotNull(message = "Order must contain at least one item")
	@OneToMany(
			mappedBy = "order", 
			cascade = CascadeType.ALL, 
			orphanRemoval = true, 
			fetch = FetchType.LAZY
	)
	private List<OrderItem> orderItems;
}