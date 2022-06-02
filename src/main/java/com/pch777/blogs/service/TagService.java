package com.pch777.blogs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Tag;
import com.pch777.blogs.repository.TagRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TagService {

	private final TagRepository tagRepository;
	
	public void addTag(String name) {
		if(!tagRepository.existsByName(name)) {
			Tag tag = new Tag();
			tag.setName(name);
			tagRepository.save(tag);
		}		
	}
	
	public List<Tag> findAllTags() {
		return tagRepository.findAll();
	}

	public Optional<Tag> findTagByName(String tagName) {
		return tagRepository.findTagByName(tagName);
	}
}
