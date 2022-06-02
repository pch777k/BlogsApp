package com.pch777.blogs.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BlogService {
	
	private final BlogRepository blogRepository;
	private final UserEntityRepository userRepository;
	
	public boolean isUserHasBlog() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		UserEntity user = userRepository
				.findByUsername(auth.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + auth.getName() + " not found."));
		System.out.println("user: " + auth.getName());
		Optional<Blog> blog = blogRepository.findByUser(user);
		return blog.isPresent();
//		return true;
	}
	
	public Optional<Blog> getBlogById(Long id) {
		return blogRepository.findById(id);
	}
	
	public BlogDto blogToBlogDto(Blog blog) {
		BlogDto blogDto = new BlogDto();
		blogDto.setName(blog.getName());
		blogDto.setDescription(blog.getDescription());
		return blogDto;
	}

	public void deleteBlogById(Long blogId) {
		blogRepository.deleteById(blogId);		
	}
	
	public Page<Blog> getAllBlogsByNameLike(Pageable pageable, String keyword) {
		return blogRepository.findByNameLike(pageable, "%" + keyword.toLowerCase() + "%");
	}
	
	
}
