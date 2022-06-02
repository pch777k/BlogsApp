package com.pch777.blogs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pch777.blogs.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
	Optional<Tag> findTagByName(String name);
	Boolean existsByName(String name);
	
	@Override
	@Query("select distinct t from Tag t left join fetch t.articles")
	List<Tag> findAll();

}
