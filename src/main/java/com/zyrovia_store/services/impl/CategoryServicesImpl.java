package com.zyrovia_store.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zyrovia_store.dtos.CategoryRequestDto;
import com.zyrovia_store.dtos.CategoryResponseDto;
import com.zyrovia_store.entities.Category;
import com.zyrovia_store.exceptions.ResourceNotFoundException;
import com.zyrovia_store.repositories.CategoryRepository;
import com.zyrovia_store.services.ICategoryServices;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServicesImpl implements ICategoryServices {

	// Repository for Category database operations
	private final CategoryRepository categoryRepository;

	// Used for Entity <-> DTO conversion
	private final ModelMapper mapper;

	// Converts Product Entity to ProductResponseDto
	private CategoryResponseDto mapToResponseDto(Category category) {

		return this.mapper.map(category, CategoryResponseDto.class);
	}

	// Create a new Category
	@Override
	public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {

		Category category = this.mapper.map(categoryRequestDto, Category.class);

		Category savedCategory = this.categoryRepository.save(category);

		return mapToResponseDto(savedCategory);
	}

	// get Category by category id
	@Override
	public CategoryResponseDto getCategoryById(Long id) {

		Category category = this.categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id :" + id));

		return mapToResponseDto(category);
	}

	// get all Categories
	@Override
	public List<CategoryResponseDto> getAllCategories() {

		return this.categoryRepository.findAll().stream().map(this::mapToResponseDto).toList();

	}

	// update an existing Category
	@Override
	public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {

		// find existing Category
		Category category = this.categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id :" + id));

		// update Category fields
		category.setCategoryName(categoryRequestDto.getCategoryName());

		Category updatedCategory = this.categoryRepository.save(category);

		return mapToResponseDto(updatedCategory);
	}

	// delete Category by Id
	@Override
	public void deleteCategory(Long id) {

		Category category = this.categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id :" + id));

		this.categoryRepository.delete(category);
	}

}
