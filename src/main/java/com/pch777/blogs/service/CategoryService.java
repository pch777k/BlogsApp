package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Category;
import com.pch777.blogs.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
	public void addCategory(String name) {
		if(!categoryRepository.existsByName(name)) {
			Category category = new Category();
			category.setName(name);
			categoryRepository.save(category);
		}		
	}
	
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}
	
	public Optional<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}
}
