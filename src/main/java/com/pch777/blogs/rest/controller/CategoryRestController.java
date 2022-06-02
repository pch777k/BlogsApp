package com.pch777.blogs.rest.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.dto.CategoryDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.repository.CategoryRepository;
import com.pch777.blogs.response.MessageResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryRestController {

	private final CategoryRepository categoryRepository;
	
	@Transactional
	@PostMapping
	public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryDto) {
		Optional<Category> categoryOpt = categoryRepository
				.findByName(categoryDto.getName());
		if(categoryOpt.isPresent()) {
			return ResponseEntity
			          .badRequest()
			          .body(new MessageResponse("Error: Category " + categoryDto.getName() + " exists!"));
		}
		Category category = new Category();
		category.setName(categoryDto.getName());
		categoryRepository.save(category);
		return ResponseEntity.ok(category);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCategoryById(@PathVariable Long id) throws ResourceNotFoundException {
		Category category =  categoryRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException());
		return ResponseEntity.ok(category);
		
	}
}
