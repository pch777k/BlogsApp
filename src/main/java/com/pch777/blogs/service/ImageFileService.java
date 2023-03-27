package com.pch777.blogs.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	public ImageFile multipartToImageFile(MultipartFile multipartFile) throws IOException {
		ImageFile imageFile = new ImageFile();
		imageFile.setFilename(multipartFile.getOriginalFilename());
		imageFile.setFile(multipartFile.getBytes());
		imageFile.setContentType(multipartFile.getContentType());
		imageFile.setFileLength(multipartFile.getSize());
		imageFile.setCreatedAt(LocalDate.now());
		imageRepository.save(imageFile);
		return imageFile;
	}
}
