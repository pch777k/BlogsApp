package com.pch777.blogs.service;

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
		if(!tagRepository.existsByName(name)) {
			Tag tag = new Tag();
			tag.setName(name);
			tagRepository.save(tag);
		}		
	}
	
	public List<Tag> findAllTags() {
		return tagRepository.findAll();
	}
	
	public List<String> getAllTagsName() {
		return tagRepository.findAll()
				.stream()
				.map(t -> t.getName())
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
}
