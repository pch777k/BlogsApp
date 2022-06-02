package com.pch777.blogs.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BlogDto {

	@NotBlank(message = "Name may not be blank")
	private String name;
	
	@NotBlank(message = "Description may not be blank")
	private String description;
}
