package com.pch777.blogs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.UserEntity;

public interface BlogRepository extends JpaRepository<Blog, Long> {
	Boolean existsByName(String name);
	
	Optional<Blog> findByUser(UserEntity user);
	
//	@Override
//	@Query("select distinct b "
//			+ "from Blog b "
//			+ "join fetch b.articles")
	List<Blog> findAll();
	
	@Query ("SELECT DISTINCT b "
			+ " FROM Blog b "
			+ " WHERE lower(b.name) LIKE %?1% "
			+ " OR lower(b.description) LIKE %?1%")
	Page<Blog> findByNameLike(Pageable pageable, String keyword);
}
