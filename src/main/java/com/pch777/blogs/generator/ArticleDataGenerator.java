package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.service.ImageFileService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ArticleDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	
	private static final String ARTICLE_TITLES_FILE_PATH = "articleTitles";
	private static final String ARTICLE_SUMMARIES_FILE_PATH = "articleSummaries";
	private static final String IMAGE_FILE_PATH = "images";
	
	private static final int MINUTES_RANGE = 525600;
	
    private List<String> articleTitles;
    private List<String> articleSummaries;
    private List<String> imagePaths;

    private final Random random = new Random();    
	
    public Article generateArticle() throws IOException {
    	Article article = new Article();
    	article.setTitle(getRandomArticleTitle());
    	article.setSummary(getRandomArticleSummaries());
    	article.setImage(getRandomImage());
    	article.setCreatedAt(getRandomArticleDate());
        return article;
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
    
    private String getRandomArticleTitle() {
        return getRandom(getArticleTitles());
    }

    private String getRandomArticleSummaries() {
        return getRandom(getArticleSummaries());
    }
    
    private String getRandom(List<String> elements) {
        String randomString = elements.get(
                random.nextInt(
                        elements.size()));
        elements.remove(randomString);
        return randomString;
    }
    
	private List<String> getArticleTitles() {
        if (articleTitles.isEmpty()) {
        	articleTitles = generatorMethods.loadLines(ARTICLE_TITLES_FILE_PATH);
        }
        return articleTitles;
    }

    private List<String> getArticleSummaries() {
        if (articleSummaries.isEmpty()) {
        	articleSummaries = generatorMethods.loadLines(ARTICLE_SUMMARIES_FILE_PATH);
        }
        return articleSummaries;
    }
    
    private List<String> getImagesPath() {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(IMAGE_FILE_PATH);
        }
        return imagePaths;
    }
	
	private LocalDateTime getRandomArticleDate() {
        return LocalDateTime
                .now().minusMinutes(
                        getRandomNumberOfMinutes());
    }

    private long getRandomNumberOfMinutes() {
        return random
        		.nextLong(MINUTES_RANGE);
    }
}
