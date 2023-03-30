package com.pch777.blogs.rest.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;
import com.pch777.blogs.response.MessageResponse;
import com.pch777.blogs.security.UserSecurity;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("api/blogs")
@AllArgsConstructor
@RestController
public class BlogRestController {

	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	private final UserSecurity userSecurity;
	private final ImageFileService imageFileService;
	
	@GetMapping
	public ResponseEntity<List<Blog>> getAllBlogs() {
		return ResponseEntity.ok(blogRepository.findAll()); 
	}
	
	@GetMapping("/{blogId}")
	public ResponseEntity<Blog> getBlogById(@PathVariable Long blogId) throws ResourceNotFoundException {
		Blog blog = blogRepository
				.findById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with " + blogId + " not found."));
		return ResponseEntity.ok(blog); 
	}
	
	@Transactional
	@PostMapping
	public ResponseEntity<Object> createBlog(@Valid @RequestBody BlogDto blogDto, Principal principal) {
		boolean blogNameExists = blogRepository.existsByName(blogDto.getName());
		
		if(blogNameExists) {
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
	
	@Transactional
	@PutMapping("/{blogId}")
	public ResponseEntity<Object> updateBlog(@Valid @RequestBody BlogDto blogDto, 
			@PathVariable Long blogId, Principal principal) throws ResourceNotFoundException {
		
		Blog blog = blogRepository
				.findById(blogId)
				.orElseThrow(() -> new ResourceNotFoundException("Blog with id " + blogId + " not found."));

		if(!userSecurity.isOwnerOrAdmin(blog.getUser().getUsername(), principal.getName())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		if(!blogDto.getName().equalsIgnoreCase(blog.getName())) {
			boolean blogNameExists = blogRepository.existsByName(blogDto.getName());
			
			if(blogNameExists) {
				return ResponseEntity
				          .badRequest()
				          .body(new MessageResponse("Error: Blog name is already taken!"));
			}
		}
		blog.setName(blogDto.getName());
		blog.setDescription(blogDto.getDescription());
		blogRepository.save(blog);
		return ResponseEntity.ok(blog);	

	}
	
	
	@DeleteMapping("/{blogId}")
	public ResponseEntity<Object> deleteBlogById(@PathVariable Long blogId, Principal principal) {
		return blogRepository
				.findById(blogId)
				.map(blog -> {
					if(userSecurity.isOwnerOrAdmin(blog.getUser().getUsername(), principal.getName())) {
						blogRepository.deleteById(blogId);
						return ResponseEntity.noContent().build();
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
					}
				}).orElse(ResponseEntity.notFound().build());
	}
	
	@Transactional
	@PostMapping(value = "/{blogId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> addPhotoToBlog(@PathVariable Long blogId, 
			 @RequestParam(value = "file") MultipartFile multipartFile, Principal principal) {

		return blogRepository.findById(blogId)
				.map(blog -> {
                    if (userSecurity.isOwnerOrAdmin(blog.getUser().getUsername(), principal.getName())) {
                    	if(!multipartFile.isEmpty()) {
							try {
								ImageFile imageFile = imageFileService.multipartToImageFile(multipartFile);
								blog.setImage(imageFile);
							} catch (IOException e) {
								log.error(e.getMessage());
							}	  		
                    	}                  	
                        return ResponseEntity.accepted().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                })
				.orElse(ResponseEntity.notFound().build());
	}
		
}
