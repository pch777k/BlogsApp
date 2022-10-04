package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.Blog;
import com.pch777.blogs.model.Category;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.model.Tag;
import com.pch777.blogs.model.UserEntity;
import com.pch777.blogs.service.ImageFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final ArticleValuesProperties articleValuesProperties; 	
	
    public Article generateArticle(Blog blog, UserEntity user, Category category, Set<Tag> tags) throws IOException {
    	Article article = new Article();
    	article.setTitle(getRandomArticleTitle());
    	article.setSummary(getRandomArticleSummaries());
    	article.setContent(getRandomArticleContents());
    	article.setImage(getRandomImage());
    	article.setCreatedAt(getRandomArticleDate());
    	article.setBlog(blog);
    	article.setUser(user);
    	article.setCategory(category);
    	article.setTags(tags);
        return article;
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
    
    private String getRandomArticleTitle() throws IOException {
        return generatorMethods.getRandomItemFromList(getArticleTitles());
    }

    private String getRandomArticleSummaries() throws IOException {
        return generatorMethods.getRandomItemFromList(getArticleSummaries());
    }
    
    private String getRandomArticleContents() throws IOException {
        return generatorMethods.getRandomItemFromList(getArticleContents());
    }
    
	private List<String> getArticleTitles() throws IOException {
		List<String> articleTitles = new ArrayList<>();
        if (articleTitles.isEmpty()) {
        	articleTitles = generatorMethods.loadLines(articleValuesProperties.getArticleTitlesFilePath());
        }
        return articleTitles;
    }

    private List<String> getArticleSummaries() throws IOException {
    	List<String> articleSummaries = new ArrayList<>();
        if (articleSummaries.isEmpty()) {
        	articleSummaries = generatorMethods.loadLines(articleValuesProperties.getArticleSummariesFilePath());
        }
        return articleSummaries;
    }
    
    private List<String> getArticleContents() throws IOException {
    	List<String> articleContents = new ArrayList<>();
        if (articleContents.isEmpty()) {
        	articleContents = generatorMethods.loadLines(articleValuesProperties.getArticleContentsFilePath());
        }
        return articleContents;
    }
    
    private List<String> getImagesPath() throws IOException {
    	List<String> imagePaths = new ArrayList<>();
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(articleValuesProperties.getArticleImagesFilePath());
        }
        return imagePaths;
    }
	
	private LocalDateTime getRandomArticleDate() {
        return LocalDateTime
                .now().minusMinutes(
                		generatorMethods.getRandomNumberOfMinutes(articleValuesProperties.getMinutesRangeForPastArticles()));
    }

    
}
