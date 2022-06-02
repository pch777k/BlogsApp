package com.pch777.blogs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String message;

}
