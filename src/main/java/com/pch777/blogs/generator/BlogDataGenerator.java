package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.service.ImageFileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlogDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;	
	private final BlogValuesProperties blogValuesProperties;

	public Blog generateBlog() throws IOException {
    	Blog blog = new Blog();
        blog.setName(getRandomBlogName());
        blog.setDescription(getRandomBlogDescription());
        blog.setImage(getRandomImage());
        return blog;
    }  
    
    private ImageFile getRandomImage() throws IOException {
    	String imagePath = generatorMethods.getRandomItemFromList(getImagesPath());
    	ClassPathResource resource = new ClassPathResource(imagePath);

    	InputStream inputStream = resource.getInputStream();
    	File file = new File(imagePath);
    	FileUtils.copyInputStreamToFile(inputStream, file);
    	
    	ImageFile imageFile = new ImageFile();
       	if(file.exists()) {      		
       		imageFile.setFile(Files.readAllBytes(file.toPath()));
       		imageFile.setFilename(file.getName());
       		imageFile.setFileLength(file.length());     	
       		imageFileService.saveImageFile(imageFile);    				
       	}
       	return imageFile;
    }
    
    private String getRandomBlogName() throws IOException {
        return generatorMethods
        		.getRandomItemFromList(getBlogNames());
    }
    
    private String getRandomBlogDescription() throws IOException {
        return generatorMethods.getRandomItemFromList(getBlogDescriptions());
    }
       
	private List<String> getBlogNames() throws IOException {
		List<String> blogNames = new ArrayList<>();
        if (blogNames.isEmpty()) {
            blogNames = generatorMethods.loadLines(blogValuesProperties.getBlogNamesFilePath());
        }
        return blogNames;
    }

    private List<String> getBlogDescriptions() throws IOException {
    	List<String> blogDescriptions = new ArrayList<>();
        if (blogDescriptions.isEmpty()) {
        	blogDescriptions = generatorMethods.loadLines(blogValuesProperties.getBlogDescriptionsFilePath());
        }
        return blogDescriptions;
    }
    
    private List<String> getImagesPath() throws IOException {
    	List<String> imagePaths = new ArrayList<>();
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(blogValuesProperties.getBlogImageFilePath());
        }
        return imagePaths;
    }

}
