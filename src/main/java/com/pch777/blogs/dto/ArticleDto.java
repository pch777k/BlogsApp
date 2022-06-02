package com.pch777.blogs.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;



import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleDto {
	
	@NotBlank(message = "Title may not be blank")
	@Size(max=70, message = "Title cannot be longer than 70 characters")
	private String title;
	
	@NotBlank(message = "Summary may not be blank")
	@Size(max=250, message = "Summary cannot be longer than 250 characters")
	private String summary;
	
	@NotBlank(message = "Content may not be blank")
	private String content;
	
	@NotEmpty(message = "Category may not be empty, please select one")
	private String categoryName;
	
	@NotEmpty(message = "Tag may not be empty, please select at least one")
	private Set<String> tagsDto;
	
}
