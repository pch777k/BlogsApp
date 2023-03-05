package com.pch777.blogs.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.repository.ImageFileRepository;

@Import({ ImageFileService.class })
@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext
class ImageFileServiceTest {

	@Autowired
	private ImageFileRepository imageFileRepository;

	@Autowired
	private ImageFileService imageFileService;

	@Test
	void shouldSaveImageFile() {
		// given
		ImageFile imageFile = new ImageFile();

		// when
		imageFileService.saveImageFile(imageFile);
		List<ImageFile> files = imageFileRepository.findAll();

		// then
		assertEquals(1, files.size());

	}

	@Test
	void shouldGetImageById() {
		// given
		ImageFile imageFile = new ImageFile();
		imageFileRepository.save(imageFile);
		// when
		Optional<ImageFile> imageFileResult = imageFileService.getImageById(imageFile.getId());

		// then
		assertTrue(imageFileResult.isPresent());
	}

	@Test
	void shouldGetImageByFilename() {
		// given
		ImageFile imageFile = new ImageFile();
		imageFile.setFilename("fileName 1");
		imageFileRepository.save(imageFile);
		// when
		Optional<ImageFile> imageFileResult = imageFileService.getImageByFilename(imageFile.getFilename());

		// then
		assertTrue(imageFileResult.isPresent());
	}

}
