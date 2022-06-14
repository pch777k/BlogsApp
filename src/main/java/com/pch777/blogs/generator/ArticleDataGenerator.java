package com.pch777.blogs.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.pch777.blogs.model.Article;
import com.pch777.blogs.model.ImageFile;
import com.pch777.blogs.service.ImageFileService;

@Service
@PropertySource("classpath:values.properties")
public class ArticleDataGenerator {

	private final ImageFileService imageFileService;
	private final GeneratorMethods generatorMethods;
	private final String articleTitlesFilePath;
	private final String articleSummariesFilePath;
	private final String articleContentsFilePath;
	private final String articleImagesFilePath;
	private final int minutesRangeForPastArticles;
	private List<String> articleTitles;
    private List<String> articleSummaries;
    private List<String> articleContents;
    private List<String> imagePaths;
	
	public ArticleDataGenerator(ImageFileService imageFileService, 
			GeneratorMethods generatorMethods, 
			@Value("${articleTitlesFilePath}") String articleTitlesFilePath, 
			@Value("${articleSummariesFilePath}") String articleSummariesFilePath, 
			@Value("${articleContentsFilePath}") String articleContentsFilePath,
			@Value("${articleImagesFilePath}") String articleImagesFilePath, 
			@Value("${minutesRangeForPastArticles}") int minutesRangeForPastArticles,
			List<String> articleTitles,
		    List<String> articleSummaries,
		    List<String> articleContents,
		    List<String> imagePaths) {
		this.imageFileService = imageFileService;
		this.generatorMethods = generatorMethods;
		this.articleTitlesFilePath = articleTitlesFilePath;
		this.articleSummariesFilePath = articleSummariesFilePath;
		this.articleContentsFilePath = articleContentsFilePath;
		this.articleImagesFilePath = articleImagesFilePath;
		this.minutesRangeForPastArticles = minutesRangeForPastArticles;
		this.articleTitles = articleTitles;
		this.articleSummaries = articleSummaries;
		this.articleContents = articleContents;
		this.imagePaths = imagePaths;
	}     
	
    public Article generateArticle() throws IOException {
    	Article article = new Article();
    	article.setTitle(getRandomArticleTitle());
    	article.setSummary(getRandomArticleSummaries());
    	article.setContent(getRandomArticleContents());
    	article.setImage(getRandomImage());
    	article.setCreatedAt(getRandomArticleDate());
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
        if (articleTitles.isEmpty()) {
        	articleTitles = generatorMethods.loadLines(articleTitlesFilePath);
        }
        return articleTitles;
    }

    private List<String> getArticleSummaries() throws IOException {
        if (articleSummaries.isEmpty()) {
        	articleSummaries = generatorMethods.loadLines(articleSummariesFilePath);
        }
        return articleSummaries;
    }
    
    private List<String> getArticleContents() throws IOException {
        if (articleContents.isEmpty()) {
        	articleContents = generatorMethods.loadLines(articleContentsFilePath);
        }
        return articleContents;
    }
    
    private List<String> getImagesPath() throws IOException {
        if (imagePaths.isEmpty()) {
        	imagePaths = generatorMethods.loadLines(articleImagesFilePath);
        }
        return imagePaths;
    }
	
	private LocalDateTime getRandomArticleDate() {
        return LocalDateTime
                .now().minusMinutes(
                		generatorMethods.getRandomNumberOfMinutes(minutesRangeForPastArticles));
    }

    
}
