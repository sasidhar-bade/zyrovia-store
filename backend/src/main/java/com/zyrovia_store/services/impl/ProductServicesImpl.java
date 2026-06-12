package com.zyrovia_store.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.ProductRequestDto;
import com.zyrovia_store.dtos.ProductResponseDto;
import com.zyrovia_store.entities.Category;
import com.zyrovia_store.entities.Product;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.CategoryRepository;
import com.zyrovia_store.repositories.ProductRepository;
import com.zyrovia_store.services.IProductServices;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServicesImpl implements IProductServices {

	// Repository for Product database operations
	private final ProductRepository productRepository;

	// Repository for Category database operations
	private final CategoryRepository categoryRepository;

	// Used for Entity <-> DTO conversion
	private final ModelMapper mapper;

	// Converts Product Entity to ProductResponseDto
	private ProductResponseDto mapToResponseDto(Product product) {

		ProductResponseDto productResponseDto = this.mapper.map(product, ProductResponseDto.class);

		// Manually set category details because they belong
		// to a related entity (Category)
		productResponseDto.setCategoryId(product.getCategory().getId());

		productResponseDto.setCategoryName(product.getCategory().getCategoryName());

		return productResponseDto;
	}

	// Create a new product
	@Override
	public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {

		// Validate Category existence
		Category category = this.categoryRepository.findById(productRequestDto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Category not found with id : " + productRequestDto.getCategoryId()));

//		// Convert DTO to Entity
//		Product product = mapper.map(productRequestDto, Product.class);

		Product product = new Product();

		product.setName(productRequestDto.getName());
		product.setDescription(productRequestDto.getDescription());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
		product.setImageUrl(productRequestDto.getImageUrl());

		// Set category relationship
		product.setCategory(category);

		Product savedProduct = this.productRepository.save(product);

		return mapToResponseDto(savedProduct);
	}

	// Get product by ID
	@Override
	public ProductResponseDto getProductById(Long productId) {

		Product product = this.productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));

		return mapToResponseDto(product);
	}

	// Get all products
	@Override
	public List<ProductResponseDto> getAllProducts() {

		return this.productRepository.findAll().stream().map(this::mapToResponseDto).toList();
	}

	// Search products by name
	@Override
	public List<ProductResponseDto> searchProducts(String keyword) {

		return this.productRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::mapToResponseDto)
				.toList();
	}

	// Update an existing product
	@Override
	public ProductResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto) {

		// Find existing product
		Product product = this.productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));

		// Validate category
		Category category = this.categoryRepository.findById(productRequestDto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Category not found with id : " + productRequestDto.getCategoryId()));

		// Update product fields
		product.setName(productRequestDto.getName());
		product.setDescription(productRequestDto.getDescription());
		product.setPrice(productRequestDto.getPrice());
		product.setStock(productRequestDto.getStock());
		product.setImageUrl(productRequestDto.getImageUrl());
		product.setCategory(category);

		Product updatedProduct = this.productRepository.save(product);

		return mapToResponseDto(updatedProduct);
	}

	// Delete product by ID
	@Override
	public void deleteProduct(Long productId) {

		Product product = this.productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + productId));

		this.productRepository.delete(product);
	}

}
