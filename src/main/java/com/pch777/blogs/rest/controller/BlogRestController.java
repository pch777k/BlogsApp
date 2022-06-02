package com.pch777.blogs.rest.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.response.MessageResponse;
import com.pch777.blogs.service.BlogService;

import lombok.AllArgsConstructor;

@RequestMapping("api/blogs")
@AllArgsConstructor
@RestController
public class BlogRestController {

	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	
	
	@Transactional
	@PostMapping
	public ResponseEntity<?> createBlog(@Valid @RequestBody BlogDto blogDto, Principal principal) {
		if(blogRepository.existsByName(blogDto.getName())) {
			return ResponseEntity
			          .badRequest()
			          .body(new MessageResponse("Error: Blog name is already taken!"));
		}
		Blog blog = new Blog();
		blog.setName(blogDto.getName());
		blog.setDescription(blogDto.getDescription());
		UserEntity user = userRepository
				.findByUsername(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + principal.getName() + " not found"));
		blog.setUser(user);
		blogRepository.save(blog);
		return ResponseEntity.ok(blog);
		
		
	}
}
