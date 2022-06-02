package com.pch777.blogs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pch777.blogs.model.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long>{
	Optional<ImageFile> findByFilename(String filename);
}
