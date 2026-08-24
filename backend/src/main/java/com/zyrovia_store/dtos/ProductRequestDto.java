package com.zyrovia_store.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

	@NotBlank(message = "Product name is required")
	private String name;
	
	@NotBlank(message = "Product description name is required")
	private String description;

	@NotBlank(message = "Price name is required")
	@DecimalMin(
				value = "0.01",
				message = "Price must be greater than zero"
			)
	private BigDecimal price;

	@NotBlank(message = "Stock name is required")
	@Min(
			value = 0,
			message = "Stock cannot be negative"
		)
	private Integer stock;

	@NotBlank(message = "ImageUrl name is required")
	private String imageUrl;

	@NotBlank(message = "CategoryId name is required")
	private Long categoryId;
}