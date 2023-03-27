package com.pch777.blogs.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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
import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.security.UserEntityDetailsService;

@WebMvcTest({BlogRestController.class})
@Import(SecurityConfig.class)
class BlogRestControllerTest {
	
	private static final String BLOGS_BASE_PATH ="/api/blogs/";

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	private BlogRepository blogRepository;
	
	@MockBean
	private UserEntityDetailsService userDetailsService;
	
	@MockBean
	private UserEntityRepository userEntityRepository;
	
	@Test
	void shouldGetAllBlogs() throws Exception {
		Blog firstBlog = givenBlog(1L, "awesome name", "awesome description");
		Blog secondBlog = givenBlog(2L, "interesting name", "interesting description");
		
		when(blogRepository.findAll()).thenReturn(List.of(firstBlog, secondBlog));
		
		mockMvc.perform(get(BLOGS_BASE_PATH))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].name").value("awesome name"))
		.andExpect(jsonPath("$.[1].description").value("interesting description"))
		.andExpect(jsonPath("$", hasSize(2)));
		
	}
	
	@Test
	void shouldGetBlogById() throws Exception {
		Blog firstBlog = givenBlog(1L, "awesome name", "awesome description");
		
		when(blogRepository.findById(anyLong())).thenReturn(Optional.of(firstBlog));
		
		mockMvc.perform(get(BLOGS_BASE_PATH + "1"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").value(1L))
		.andExpect(jsonPath("$.name").value("awesome name"))
		.andExpect(jsonPath("$.description").value("awesome description"));
	}
	
	@Test
	void shouldNotGetBlogById() throws Exception {
		
		when(blogRepository.findById(100L)).thenReturn(Optional.ofNullable(null));
		
		 mockMvc.perform(get(BLOGS_BASE_PATH + "100"))
         .andExpect(status().isNotFound())
         .andExpect(result -> assertEquals("Blog with 100 not found.", result.getResolvedException().getMessage()));;
	}

	@Test
	@WithMockUser
	void shouldCreateBlog() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("name of blog", "description of blog");
		
		when(blogRepository.existsByName(blogDto.getName())).thenReturn(false);
		when(userEntityRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value("name of blog"))
		.andExpect(jsonPath("$.description").value("description of blog"));
	}
	
	@Test
	void shouldNotCreateBlogIfUserIsUnauthorized() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("name of blog", "description of blog");
		
		when(blogRepository.existsByName(blogDto.getName())).thenReturn(false);
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser
	void shouldNotCreateBlogIfBlogAlreadyExists() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("name of blog", "description of blog");
		
		when(blogRepository.existsByName(blogDto.getName())).thenReturn(true);
		when(userEntityRepository.findByUsername("user")).thenReturn(Optional.of(new UserEntity()));
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Error: Blog name is already taken!"));
	}
	
	@Test
	@WithMockUser
	void shouldNotCreateBlogIfBlogNameAndBlogDescriptionAreEmpty() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("", "");
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors", hasSize(2)));

	}
	
	@Test
	@WithMockUser
	void shouldNotCreateBlogIfBlogNameIsEmpty() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("", "description of blog");
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors", hasSize(1)))
		.andExpect(jsonPath("$.errors[0]").value("name - Name may not be blank"));
	}
	
	@Test
	@WithMockUser
	void shouldNotCreateBlogIfBlogDescriptionIsEmpty() throws JsonProcessingException, Exception {
		BlogDto blogDto = givenBlogDto("name of blog", "");
		
		mockMvc.perform(post(BLOGS_BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blogDto)))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errors", hasSize(1)))
		.andExpect(jsonPath("$.errors[0]").value("description - Description may not be blank"));
	}
	
	private Blog givenBlog(Long id, String name, String description) {
		Blog blog = new Blog();
		blog.setId(id);
		blog.setName(name);
		blog.setDescription(description);
		return blog;
	}
	
	private BlogDto givenBlogDto(String name, String description) {
		BlogDto blogDto = new BlogDto();
		blogDto.setName(name);
		blogDto.setDescription(description);
		return blogDto;
	}
	

}
