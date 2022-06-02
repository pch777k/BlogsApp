package com.pch777.blogs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pch777.blogs.model.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsername(String username);
	Boolean existsByUsername(String username);
	
	@Override
	@Query("select distinct u "
			+ " from UserEntity u "
			+ " left join fetch u.roles "
			+ " left join fetch u.articles "
			+ " left join fetch u.comments ")
	List<UserEntity> findAll();
}
