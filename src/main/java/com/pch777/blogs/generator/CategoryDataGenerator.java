package com.pch777.blogs.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Category;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryDataGenerator {
	
	private final GeneratorMethods generatorMethods;
	
	private static final String CATEGORIES_FILE_PATH = "categories";
	private static final String TAG_DIRECTORY_PATH = "files/";
	
    private List<String> categories;
    private List<String> tags;

    private final Random random = new Random();    
	
//    public Map<Category,Set<String>> generateCategory() throws IOException {
//    	Map<Category,Set<String>> map = new HashMap<>();
//    	Category category = new Category();
//    	category.setName(getRandomCategoryName());
//    	Set<String> tags = getRandomTagNames(category.getName());
//    	map.put(category, tags);
//        return map;
//    }  
    public Category generateCategory() throws IOException { 
    	Category category = new Category();
    	category.setName(getRandomCategoryName());  	
        return category;
    }  
    
//    public Set<Tag> generateTagsByCategoryName(String categoryName) throws IOException { 
//    	Set<String> tagNames = getRandomTagNames(categoryName);
//    	return tagNames
//    			.stream()
//    			.map(name -> new Tag(name))
//    			.collect(Collectors.toSet());
//    }  
    
    public Set<String> generateTagsByCategoryName(String categoryName) throws IOException { 
    	return  getRandomTagNames(categoryName);
    }
    
    private String getRandomCategoryName() {
        return getRandom(getCategoryNames());
    }
    
    private Set<String> getRandomTagNames(String categoryName) {
    	Set<String> tags = new HashSet<>();
    	for(int i=0; i < random.nextInt(1, 6); i++) {
    		tags.add(getRandom(getTagNames(categoryName)));
    	}
        return tags;
    }

    private String getRandom(List<String> elements) {
        return elements.get(
                random.nextInt(
                        elements.size()));
    }
    
	private List<String> getCategoryNames() {
        if (categories.isEmpty()) {
        	categories = generatorMethods.loadLines(CATEGORIES_FILE_PATH);
        }
        return categories;
    }
	
	private List<String> getTagNames(String categoryName) {
		tags.clear();
        if (tags.isEmpty()) {
        	tags = generatorMethods.loadLines(TAG_DIRECTORY_PATH + categoryName.toLowerCase());
        }
        return tags;
    }
	
//	private List<String> loadLines(String filePath) {
//        try {
//            return Files.readAllLines(
//                    Paths.get(
//                            new ClassPathResource(
//                                    filePath)
//                                    .getURI()));
//        } catch (IOException e) {
//            throw new RuntimeException(
//                    String.format(
//                            "Error while reading lines from file %s",
//                            filePath),
//                    e);
//        }
//    }
}
