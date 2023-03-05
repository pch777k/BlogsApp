package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.repository.BlogRepository;
import com.pch777.blogs.repository.UserEntityRepository;

@Import({ BlogService.class })
@DataJpaTest
@AutoConfigureTestDatabase
class BlogServiceTest {

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private UserEntityRepository userRepository;

	@Autowired
	private BlogService blogService;

	@Test
	void shouldGetBlogById() {
		// given
		Blog blog = givenBlog("Blog 1", "Description 1");
		// when
		Optional<Blog> resultBlog = blogService.getBlogById(blog.getId());
		// then
		assertTrue(resultBlog.isPresent());
		assertEquals("Blog 1", resultBlog.get().getName());
	}
	
	@Test
	void shouldNotFindBlogById() {
		givenBlogData("Blog 1", "Description 1");
		// when
		Optional<Blog> resultBlog = blogService.getBlogById(10L);
		// then
		assertFalse(resultBlog.isPresent());

	}

	@Test
	void shouldDeletingBlogById() {
		// given
		Blog blog = givenBlog("Blog 1", "Description 1");
		// when
		blogService.deleteBlogById(blog.getId());
		List<Blog> blogs = blogService.findAllBlogs();
		// then
		assertEquals(0, blogs.size());
		assertTrue(blogs.isEmpty());
	}

	@Test
	void shouldFindBlogByNameByEnteringAKeyword() {
		// given
		Blog blog1 = givenBlog("Blog 1", "Description 1");
		Blog blog2 = givenBlog("Java", "Description 2");

		// when
		Pageable pageable = PageRequest.of(0, 10);
		Page<Blog> blogs = blogService.getAllBlogsByNameLike(pageable, "ava");

		// then
		assertEquals(1L, blogs.getTotalElements());
		assertEquals(blog2.getName(), blogs.getContent().get(0).getName());
		assertNotEquals(blog1.getName(), blogs.getContent().get(0).getName());
	}

	@Test
	void shouldNotFindAnyBlogByNameByEnteringAKeyword() {
		givenBlogData("Blog 1", "Description 1");
		givenBlogData("Java", "Description 2");

		// when
		Pageable pageable = PageRequest.of(0, 10);
		Page<Blog> blogs = blogService.getAllBlogsByNameLike(pageable, "Spring");

		// then
		assertEquals(0, blogs.getTotalElements());
		assertTrue(blogs.isEmpty());
	}

	@Test
	void shouldFindAllBlogs() {
		// given
		givenBlogData("Blog 1", "Description 1");
		// when
		List<Blog> blogs = blogService.findAllBlogs();
		// then
		assertEquals(1, blogs.size());
	}

	@Test
	void blogShouldExists() {
		//given
		givenBlogData("Blog 1", "Description 1");
		// when
		// then
		assertTrue(blogService.isBlogExist("Blog 1"));
	}

	@Test
	void blogShouldNotExist() {
		givenBlogData("Blog 1", "Description 1");
		// when
		// then
		assertFalse(blogService.isBlogExist("Blog"));
	}

	@Test
	void shouldFindBlogByUser() {
		// given
		UserEntity user = new UserEntity();
		userRepository.save(user);
		Blog blog = new Blog();
		blog.setUser(user);
		blogRepository.save(blog);
		// when
		Optional<Blog> resultBlog = blogService.findByUser(user);
		// then
		assertTrue(resultBlog.isPresent());
	}

	@Test
	void shouldNotFindBlogByUser() {
		// given
		UserEntity user1 = new UserEntity();
		userRepository.save(user1);
		UserEntity user2 = new UserEntity();
		userRepository.save(user2);
		Blog blog = new Blog();
		blog.setUser(user1);
		blogRepository.save(blog);
		// when
		Optional<Blog> resultBlog = blogService.findByUser(user2);
		// then
		assertFalse(resultBlog.isPresent());
		assertTrue(resultBlog.isEmpty());
	}
	
	private Blog givenBlog(String name, String description) {
		UserEntity user = givenUser();
		Blog blog = new Blog(name, description);
		blog.setUser(user);
		return blogRepository.save(blog);
	}
	
	private void givenBlogData(String name, String description) {
		UserEntity user = givenUser();
		Blog blog = new Blog(name, description);
		blog.setUser(user);
		blogRepository.save(blog);
	}
	
	private UserEntity givenUser() {
		return userRepository.save(new UserEntity());
	}

}
