package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pch777.blogs.dto.BlogDto;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BlogService {
	
	private final BlogRepository blogRepository;
	
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
	
	public List<Blog> findAllBlogs() {
		return blogRepository.findAll();
	}
	
	public boolean isBlogExist(String name) {
		return blogRepository.existsByName(name);
	}
	
	public Blog save(Blog blog) {
		return blogRepository.save(blog);
	}
	
	public Optional<Blog> findByUser (UserEntity user) {
		return blogRepository.findByUser(user);
	}
}
