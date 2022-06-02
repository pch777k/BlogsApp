package com.pch777.blogs.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDto {
	
	@NotBlank(message = "Comment may not be blank")
	private String content;
}
