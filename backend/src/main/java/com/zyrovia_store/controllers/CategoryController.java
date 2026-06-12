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
import org.springframework.web.bind.annotation.RestController;

import com.zyrovia_store.dtos.CategoryRequestDto;
import com.zyrovia_store.dtos.CategoryResponseDto;
import com.zyrovia_store.services.ICategoryServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final ICategoryServices categoryServices;

	// Insert Category into category table in database API Call
	@PostMapping
	public ResponseEntity<CategoryResponseDto> createCategoryApiHandler(@RequestBody CategoryRequestDto requestDto) {

		CategoryResponseDto responseDto = this.categoryServices.createCategory(requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// get category by id API Call
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> getCategoryByIdApiHandler(@PathVariable Long id) {

		return ResponseEntity.ok(this.categoryServices.getCategoryById(id));

	}

	// getting all categories API Call
	@GetMapping
	public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesApiHandler() {

		return ResponseEntity.ok(this.categoryServices.getAllCategories());
	}

	// Update Category in category table in database API Call
	@PatchMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategoryApiHandler(@PathVariable Long id,
			@RequestBody CategoryRequestDto requestDto) {

		return ResponseEntity.ok(this.categoryServices.updateCategory(id, requestDto));
	}

	// Delete Category in category table in database API Call
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategoryApiHandler(@PathVariable Long id) {

		this.categoryServices.deleteCategory(id);

		return ResponseEntity.noContent().build();
	}

}
