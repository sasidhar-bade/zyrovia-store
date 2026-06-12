package com.zyrovia_store.services;

import java.util.List;

import com.zyrovia_store.dtos.CategoryRequestDto;
import com.zyrovia_store.dtos.CategoryResponseDto;

public interface ICategoryServices {

	CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

	CategoryResponseDto getCategoryById(Long id);

	List<CategoryResponseDto> getAllCategories();

//	List<CategoryResponseDto> searchCategory(String keyword);

	CategoryResponseDto updaCategory(Long id, CategoryRequestDto categoryRequestDto);

	void deleteCategory(Long id);

}
