package com.pch777.blogs.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@PropertySource("classpath:values.properties")
public class ArticleValuesProperties {

	private String articleTitlesFilePath;
	private String articleSummariesFilePath;
	private String articleContentsFilePath;
	private String articleImagesFilePath;
	private int minutesRangeForPastArticles;
	private int numberOfLatestArticles;
	private int numberOfTopCategories;
	private int numberOfTopTags;
	private int numberOfMostCommentedArticles;
	
	public ArticleValuesProperties(@Value("${articleTitlesFilePath}") String articleTitlesFilePath, 
			@Value("${articleSummariesFilePath}") String articleSummariesFilePath, 
			@Value("${articleContentsFilePath}") String articleContentsFilePath,
			@Value("${articleImagesFilePath}") String articleImagesFilePath, 
			@Value("${minutesRangeForPastArticles}") int minutesRangeForPastArticles,
			@Value("${numberOfLatestArticles}") int numberOfLatestArticles,
			@Value("${numberOfTopCategories}") int numberOfTopCategories,
			@Value("${numberOfTopTags}") int numberOfTopTags,
			@Value("${numberOfMostCommentedArticles}") int numberOfMostCommentedArticles) {
		this.articleTitlesFilePath = articleTitlesFilePath;
		this.articleSummariesFilePath = articleSummariesFilePath;
		this.articleContentsFilePath = articleContentsFilePath;
		this.articleImagesFilePath = articleImagesFilePath;
		this.minutesRangeForPastArticles = minutesRangeForPastArticles;
		this.numberOfLatestArticles = numberOfLatestArticles;
		this.numberOfTopCategories = numberOfTopCategories;
		this.numberOfTopTags = numberOfTopTags;
		this.numberOfMostCommentedArticles = numberOfMostCommentedArticles;
	}
	
	
	
	
}
