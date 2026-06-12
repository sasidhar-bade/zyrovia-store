package com.zyrovia_store.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	// Insert product into product table in database API Call
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProductApiHandler(
			@RequestBody ProductRequestDto productRequestDto) {

		ProductResponseDto productResponseDto = this.productServices.createProduct(productRequestDto);

		return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
	}

	// getting product by id API Call
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getProductByIdApiHandler(@PathVariable Long id) {

//		ProductResponseDto product = this.productServices.getProductById(id);
//
//		return new ResponseEntity<>(product, HttpStatus.OK);

		return ResponseEntity.ok(this.productServices.getProductById(id));
	}

	// getting all products API Call
	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProductsApiHandler() {

//		List<ProductResponseDto> products = this.productServices.getAllProducts();
//
//		return new ResponseEntity<>(products, HttpStatus.OK);

		return ResponseEntity.ok(this.productServices.getAllProducts());
	}

	// Searching products API Call
	@GetMapping("/search")
	public ResponseEntity<List<ProductResponseDto>> searchProductsApiHandler(@RequestParam String keyword) {

//		List<ProductResponseDto> products = this.productServices.searchProducts(keyword);
//
//		return new ResponseEntity<>(products, HttpStatus.OK);

		return ResponseEntity.ok(this.productServices.searchProducts(keyword));
	}

	// Update product in product table in database API Call
	@PatchMapping("/{id}")
	public ResponseEntity<ProductResponseDto> updateProductApiHandler(@PathVariable Long id,
			@RequestBody ProductRequestDto productRequestDto) {

//		ProductResponseDto productResponseDto = this.productServices.updateProduct(id, productRequestDto);
//
//		return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);

		return ResponseEntity.ok(this.productServices.updateProduct(id, productRequestDto));
	}

	// Delete product in product table in database API Call
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductApiHandler(@PathVariable Long id) {

//		this.productServices.deleteProduct(id);
//
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		this.productServices.deleteProduct(id);

		return ResponseEntity.noContent().build();
	}

}
