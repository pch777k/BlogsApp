package com.pch777.blogs.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.dto.TagDto;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.service.TagService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("api/tags")
@RestController
public class TagRestController {

	private final TagService tagService;
	
	@GetMapping()
	public ResponseEntity<List<Tag>> getAllTags() {
		return ResponseEntity.ok(tagService.findAllTags());
	}
	
	@Transactional
	@PostMapping()
	public ResponseEntity<Object> addTag(@RequestBody TagDto tagDto) {
		tagService.addTag(tagDto.getName());
		return ResponseEntity.ok().build();
	}
	
	
	
}
