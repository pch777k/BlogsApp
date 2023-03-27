package com.pch777.blogs.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;

@DataJpaTest
class BlogRepositoryTest {

	@Autowired
	private BlogRepository blogRepository;

	@Test
	void shouldFindNoBlogsIfRepositoryIsEmpty() {
		List<Blog> blogs = blogRepository.findAll();

		assertThat(blogs).isEmpty();
	}

/*	@Test
	void shouldStoreABlog() {
		UserEntity user = new UserEntity();
		Blog blog = blogRepository.save(new Blog("Blog 1", "Description 1"));
		blog.setUser(user);
		
		assertThat(blog).hasFieldOrPropertyWithValue("name", "Blog 1");
		assertThat(blog).hasFieldOrPropertyWithValue("description", "Description 1");

	} */

}
