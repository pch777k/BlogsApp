package com.pch777.blogs.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Category;

@Service
@PropertySource("classpath:values.properties")
public class CategoryDataGenerator {
	
	private final GeneratorMethods generatorMethods;
	private final String categoriesFilePath;
	private final String tagsDirectoryPath;
	private final int minNumberOfTags;
	private final int maxNumberOfTags;	
    private List<String> categories;
    private List<String> tags;    
	
    public CategoryDataGenerator(GeneratorMethods generatorMethods,  
    		@Value("${categoriesFilePath}") String categoriesFilePath,
    		@Value("${tagsDirectoryPath}") String tagsDirectoryPath, 
    		@Value("${minNumberOfTags}") int minNumberOfTags, 
    		@Value("${maxNumberOfTags}") int maxNumberOfTags, 
			List<String> categories,
			List<String> tags) {
		this.generatorMethods = generatorMethods;
		this.categoriesFilePath = categoriesFilePath;
		this.tagsDirectoryPath = tagsDirectoryPath;
		this.minNumberOfTags = minNumberOfTags;
		this.maxNumberOfTags = maxNumberOfTags;
		this.categories = categories;
		this.tags = tags;
	}

	public Category generateCategory() throws IOException { 
    	Category category = new Category();
    	category.setName(getRandomCategoryName());  	
        return category;
    }   
    
    public Set<String> generateTagsByCategoryName(String categoryName) throws IOException { 
    	return  getRandomTagNames(categoryName);
    }
    
    private String getRandomCategoryName() throws IOException {
        return generatorMethods.getRandomItemFromList(getCategoryNames());
    }
    
    private Set<String> getRandomTagNames(String categoryName) throws IOException {
    	Set<String> randomTags = new HashSet<>();
    		int randomNumberOfTags = generatorMethods
    				.randomNumberBetweenMinAndMax(minNumberOfTags, maxNumberOfTags);
    		for(int i = 0; i < randomNumberOfTags; i++) {
    			randomTags.add(generatorMethods.getRandomItemFromList(getTagNames(categoryName)));
    	}
        return randomTags;
    }
    
	private List<String> getCategoryNames() throws IOException {
        if (categories.isEmpty()) {
        	categories = generatorMethods.loadLines(categoriesFilePath);
        }
        return categories;
    }
	
	private List<String> getTagNames(String categoryName) throws IOException {
		tags.clear();
        if (tags.isEmpty()) {
        	tags = generatorMethods.loadLines(tagsDirectoryPath + categoryName.toLowerCase());
        }
        return tags;
    }

}
