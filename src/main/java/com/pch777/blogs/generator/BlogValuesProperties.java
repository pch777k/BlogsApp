package com.pch777.blogs.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@PropertySource("classpath:values.properties")
public class BlogValuesProperties {
	
	private String blogNamesFilePath;
	private String blogDescriptionsFilePath;
	private String blogImageFilePath;
	private int numberOfUsersWithoutBlog;
	private int numberOfUsersWithBlog;
	private int minNumberOfArticles;
	private int maxNumberOfArticles;
	private int minNumberOfComments;
	private int maxNumberOfComments;
	private String defaultUserAvatarFilePath;
	
	public BlogValuesProperties(@Value("${blogNamesFilePath}") String blogNamesFilePath, 
    		@Value("${blogDescriptionsFilePath}") String blogDescriptionsFilePath, 
    		@Value("${blogImageFilePath}") String blogImageFilePath,
    		@Value("${numberOfUsersWithoutBlog}") int numberOfUsersWithoutBlog,
			@Value("${numberOfUsersWithBlog}") int numberOfUsersWithBlog, 
			@Value("${minNumberOfArticles}") int minNumberOfArticles, 
			@Value("${maxNumberOfArticles}") int maxNumberOfArticles, 
			@Value("${minNumberOfComments}") int minNumberOfComments,
			@Value("${maxNumberOfComments}") int maxNumberOfComments, 
			@Value("${defaultUserAvatarFilePath}") String defaultUserAvatarFilePath) {
		this.blogNamesFilePath = blogNamesFilePath;
		this.blogDescriptionsFilePath = blogDescriptionsFilePath;
		this.blogImageFilePath = blogImageFilePath;
		this.numberOfUsersWithoutBlog = numberOfUsersWithoutBlog;
		this.numberOfUsersWithBlog = numberOfUsersWithBlog;
		this.minNumberOfArticles = minNumberOfArticles;
		this.maxNumberOfArticles = maxNumberOfArticles;
		this.minNumberOfComments = minNumberOfComments;
		this.maxNumberOfComments = maxNumberOfComments;
		this.defaultUserAvatarFilePath = defaultUserAvatarFilePath;
	}
	
}
