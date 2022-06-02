package com.pch777.blogs.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {
	@NotBlank(message = "Category name may not be blank")
	private String name;
}
