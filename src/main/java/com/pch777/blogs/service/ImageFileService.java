package com.pch777.blogs.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.repository.ImageFileRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ImageFileService {

	private ImageFileRepository imageRepository;

	public void saveImageFile(ImageFile imageFile) {
		imageRepository.save(imageFile);
	}
	
	public Optional<ImageFile> getImageById(Long id) {
		return imageRepository.findById(id);
	}
	
	public Optional<ImageFile> getImageByFilename(String filename) {
		return imageRepository.findByFilename(filename);
	}
}
