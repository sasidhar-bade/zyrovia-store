package com.zyrovia_store.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.zyrovia_store.dtos.ProductRequestDto;
import com.zyrovia_store.dtos.ProductResponseDto;

public interface IProductServices {

	ProductResponseDto createProduct(ProductRequestDto productRequestDto, Authentication authentication);

	ProductResponseDto getProductById(Long productId);

	List<ProductResponseDto> getAllProducts();

	List<ProductResponseDto> searchProducts(String keyword);

	ProductResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto);

	void deleteProduct(Long productId);

}
