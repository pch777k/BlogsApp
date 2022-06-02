package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BlogDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	
	private static final String BLOG_NAMES_FILE_PATH = "blogNames";
	private static final String BLOG_DESCRIPTIONS_FILE_PATH = "blogDescriptions";
	private static final String IMAGE_FILE_PATH = "blog-images";
	
    private List<String> blogNames;
    private List<String> blogDescriptions;
    private List<String> imagePaths;

    private final Random random = new Random();    
	
    public Blog generateBlog() throws IOException {
    	Blog blog = new Blog();
        blog.setName(getRandomBlogName());
        blog.setDescription(getRandomBlogDescription());
        blog.setImage(getRandomImage());
        return blog;
    }  
    
    private ImageFile getRandomImage() throws IOException {
    	String imagePath = getRandom(getImagesPath());
    	File file = ResourceUtils.getFile(imagePath);
    	ImageFile imageFile = new ImageFile();
       	if(file.exists()) {      		
       		imageFile.setFile(Files.readAllBytes(file.toPath()));
       		imageFile.setFilename(file.getName());
       		imageFile.setFileLength(file.length());     	
       		imageFileService.saveImageFile(imageFile);    				
       	}
       	return imageFile;
    }
    
    private String getRandomBlogName() {
        return getRandom(getBlogNames());
    }

    private String getRandomBlogDescription() {
        return getRandom(getBlogDescriptions());
    }
    
//    private String getRandomImagePath(List<String> elements) {
//        String imagePath = elements.get(
//                random.nextInt(
//                        elements.size()));
//        elements.remove(imagePath);
//        return imagePath;
//        
//    }
    
    private String getRandom(List<String> elements) {
        String randomString = elements.get(
                random.nextInt(
                        elements.size()));
        elements.remove(randomString);
        return randomString;
    }
    
	private List<String> getBlogNames() {
        if (blogNames.isEmpty()) {
            blogNames = generatorMethods.loadLines(BLOG_NAMES_FILE_PATH);
        }
        return blogNames;
    }

    private List<String> getBlogDescriptions() {
        if (blogDescriptions.isEmpty()) {
        	blogDescriptions = generatorMethods.loadLines(BLOG_DESCRIPTIONS_FILE_PATH);
        }
        return blogDescriptions;
    }
    
    private List<String> getImagesPath() {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(IMAGE_FILE_PATH);
        }
        return imagePaths;
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
