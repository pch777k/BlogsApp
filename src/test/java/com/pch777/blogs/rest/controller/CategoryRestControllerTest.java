package com.pch777.blogs.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blogs.config.SecurityConfig;
import com.pch777.blogs.dto.CategoryDto;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.repository.CategoryRepository;
import com.pch777.blogs.security.UserEntityDetailsService;

@WebMvcTest({CategoryRestController.class})
@Import(SecurityConfig.class)
class CategoryRestControllerTest {

	private static final String CATEGORIES_BASE_PATH ="/api/categories/";
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
    private CategoryRepository categoryRepository;
	
	@MockBean
	private UserEntityDetailsService userDetailsService;
	
	@Test
	@WithMockUser
	void shouldAddCategory() throws JsonProcessingException, Exception {
		CategoryDto categoryDto = givenCategoryDto("new category");
		
		when(categoryRepository.findByName(categoryDto.getName())).thenReturn(Optional.empty());
		
		mockMvc.perform(post(CATEGORIES_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value(categoryDto.getName()));
		
	}
	
	@Test
	@WithMockUser
	void shouldReturnBadRequestStatusIfCategoryExistsWhenAddingCategory() throws JsonProcessingException, Exception {
		CategoryDto categoryDto = givenCategoryDto("category");
		Category category = givenCategory(1L, "category");

		when(categoryRepository.findByName(categoryDto.getName())).thenReturn(Optional.of(category));
		
		mockMvc.perform(post(CATEGORIES_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Error: Category " + categoryDto.getName() + " exists!"));
		
	}
	
	@Test
	void shouldReturnUnauthorizedStatusWhenAddingCategory() throws JsonProcessingException, Exception {
		CategoryDto categoryDto = givenCategoryDto("new category");
		
		when(categoryRepository.findByName(categoryDto.getName())).thenReturn(Optional.empty());
		
		mockMvc.perform(post(CATEGORIES_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto)))
		.andDo(print())
		.andExpect(status().isUnauthorized());
		
	}
	
	@Test
	void shouldGetCategoryById() throws Exception {
		Category category = givenCategory(1L, "category name");
	
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		
		mockMvc.perform(get(CATEGORIES_BASE_PATH + 1))
		.andDo(print())
		.andExpect(status().isOk());
					
	}
	
	@Test
	void shouldNotGetCategoryByIdReturnNotFound() throws Exception {
	
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
		
		mockMvc.perform(get(CATEGORIES_BASE_PATH + 1))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertEquals("Category with 1 not found.", result.getResolvedException().getMessage()));
					
	}

	private Category givenCategory(Long id, String name) {
		Category category = new Category();
		category.setId(id);
		category.setName(name);
		category.setArticles(Set.of());
		return category;
	}
	
	private CategoryDto givenCategoryDto(String categoryName) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setName(categoryName);
		return categoryDto;
	}

}
