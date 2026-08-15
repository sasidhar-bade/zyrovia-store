package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.ProductRequestDto;
import com.zyrovia_store.dtos.ProductResponseDto;
import com.zyrovia_store.services.IProductServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final IProductServices productServices;

    // ADMIN and SELLER can create products
	@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProductApiHandler(
			@RequestBody ProductRequestDto productRequestDto) {

		ProductResponseDto productResponseDto = this.productServices.createProduct(productRequestDto);

		return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
	}

    // ADMIN, SELLER and USER can view products
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> getProductByIdApiHandler(@PathVariable Long productId) {

		return ResponseEntity.ok(this.productServices.getProductById(productId));
	}

    // Any authenticated user can view products
	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProductsApiHandler() {

		return ResponseEntity.ok(this.productServices.getAllProducts());
	}

    // Any authenticated user can search products
	@GetMapping("/search")
	public ResponseEntity<List<ProductResponseDto>> searchProductsApiHandler(@RequestParam String keyword) {

		return ResponseEntity.ok(this.productServices.searchProducts(keyword));
	}

	// ADMIN can update any product
    // SELLER can update only own product
	@PreAuthorize(
			"hasRole('ADMIN') or " + 
			"@productSecurity.isOwner(#productId, authentication.name)")
	@PatchMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> updateProductApiHandler(@PathVariable Long productId,
			@RequestBody ProductRequestDto productRequestDto) {

		return ResponseEntity.ok(this.productServices.updateProduct(productId, productRequestDto));
	}

	// ADMIN can delete any product
    // SELLER can delete only own product
	@PreAuthorize(
			"hasRole('ADMIN') or " + 
			"@productSecurity.isOwner(#productId, authentication.name)")
	@DeleteMapping("/{productId}")
	public ResponseEntity<Void> deleteProductApiHandler(@PathVariable Long productId) {

		this.productServices.deleteProduct(productId);

		return ResponseEntity.noContent().build();
	}
}
