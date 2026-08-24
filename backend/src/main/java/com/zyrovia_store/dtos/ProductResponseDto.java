package com.zyrovia_store.dtos;

import java.math.BigDecimal;

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
public class ProductResponseDto {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private Integer stock;

	private String imageUrl;

	private Long categoryId;

	private String categoryName;

}
