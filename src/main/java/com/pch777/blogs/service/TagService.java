package com.pch777.blogs.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Tag;
import com.pch777.blogs.repository.TagRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TagService {

	private final TagRepository tagRepository;
	
	public void addTag(String name) {
		boolean tagExists = tagRepository.existsByNameIgnoreCase(name);
		
		if(!tagExists) {
			Tag tag = new Tag();
			tag.setName(name);
			tagRepository.save(tag);
		}		
	}
	
	public void deleteTagById(Long id) {
		tagRepository.deleteById(id);
	}
	
	public List<Tag> findAllTags() {
		return tagRepository.findAll();
	}
	
	public boolean tagExists(String tagName) {
		return tagRepository.existsByNameIgnoreCase(tagName);
	}
	
	public List<String> getAllTagsName() {
		return tagRepository.findAll()
				.stream()
				.map(Tag::getName)
				.sorted()
				.collect(Collectors.toList());
	}
	
	public List<Tag> findTopTags(int numberOfTags) {
		return tagRepository
				.findAll()
				.stream()
				.sorted((o1, o2) -> {
			            if(o1.getArticles().size() == o2.getArticles().size()) return 0;
			            else if(o1.getArticles().size() < o2.getArticles().size()) return 1;
			            else return -1;
						})
				.limit(numberOfTags)
				.collect(Collectors.toList());
	}

	public Optional<Tag> findTagByName(String tagName) {
		return tagRepository.findTagByName(tagName);
	}
	
	public Set<Tag> fetchTagsByNames(Set<String> names) {
		return names.stream().map(name -> {
			addTag(name);
			return tagRepository.findTagByName(name).get();
		}).collect(Collectors.toSet());
	}

	public List<Tag> findAllTagsSorted() {
		return tagRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(Tag::getName))
				.collect(Collectors.toList());
	}
	
}
