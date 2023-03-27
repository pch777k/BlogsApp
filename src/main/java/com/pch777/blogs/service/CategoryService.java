package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Category;
import com.pch777.blogs.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
	public void addCategory(String name) {
		boolean categoryExists = categoryRepository.existsByNameIgnoreCase(name);
		
		if(!categoryExists) {
			Category category = new Category();
			category.setName(name);
			categoryRepository.save(category);
		}		
	}
	
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}
	
	public boolean categoryExists(String categoryName) {
		return categoryRepository.existsByNameIgnoreCase(categoryName);
	}
	
	public List<String> getAllCategoriesName() {
		return categoryRepository.findAll()
				.stream()
				.map(Category::getName)
				.sorted()
				.collect(Collectors.toList());
	}
	
	public List<Category> findAllCategoriesSortedByName() {
		return categoryRepository.findAllOrderByName();
	}
	
	public List<Category> findTopCategories(int numberOfCategories) {
		return categoryRepository
			.findAll()
			.stream()
			.sorted((o1, o2) -> {
		            if(o1.getArticles().size() == o2.getArticles().size()) return 0;
		            else if(o1.getArticles().size() < o2.getArticles().size()) return 1;
		            else return -1;
					})
			.limit(numberOfCategories)
			.collect(Collectors.toList());
	}
	
	public Optional<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}
	
	public Optional<Category> findById(Long id) {
		return categoryRepository.findById(id);
	}
}
