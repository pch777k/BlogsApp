package com.pch777.blogs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pch777.blogs.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByName(String name);
	Boolean existsByName(String name);
	
	@Override
	@Query("select distinct c from Category c left join fetch c.articles")
	List<Category> findAll();
}
