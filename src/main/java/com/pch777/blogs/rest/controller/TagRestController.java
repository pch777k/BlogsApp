package com.pch777.blogs.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.model.Tag;
import com.pch777.blogs.service.TagService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/tags")
@RestController
public class TagRestController {

	private final TagService tagService;
	
	@GetMapping
	public List<Tag> getAllTags() {
		return tagService.findAllTags();
	}
	
	
}
