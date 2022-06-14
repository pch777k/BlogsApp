package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.service.ImageFileService;

@Service
public class BlogDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final String blogNamesFilePath;
	private final String blogDescriptionsFilePath;
	private final String blogImageFilePath;	
    private List<String> blogNames;
    private List<String> blogDescriptions;
    private List<String> imagePaths;
    
    public BlogDataGenerator(ImageFileService imageFileService, 
    		GeneratorMethods generatorMethods, 
    		@Value("${blogNamesFilePath}") String blogNamesFilePath, 
    		@Value("${blogDescriptionsFilePath}") String blogDescriptionsFilePath, 
    		@Value("${blogImageFilePath}") String blogImageFilePath,
			List<String> blogNames, 
			List<String> blogDescriptions, 
			List<String> imagePaths) {
		this.imageFileService = imageFileService;
		this.generatorMethods = generatorMethods;
		this.blogNamesFilePath = blogNamesFilePath;
		this.blogDescriptionsFilePath = blogDescriptionsFilePath;
		this.blogImageFilePath = blogImageFilePath;
		this.blogNames = blogNames;
		this.blogDescriptions = blogDescriptions;
		this.imagePaths = imagePaths;
	}

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
        if (blogNames.isEmpty()) {
            blogNames = generatorMethods.loadLines(blogNamesFilePath);
        }
        return blogNames;
    }

    private List<String> getBlogDescriptions() throws IOException {
        if (blogDescriptions.isEmpty()) {
        	blogDescriptions = generatorMethods.loadLines(blogDescriptionsFilePath);
        }
        return blogDescriptions;
    }
    
    private List<String> getImagesPath() throws IOException {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(blogImageFilePath);
        }
        return imagePaths;
    }

}
