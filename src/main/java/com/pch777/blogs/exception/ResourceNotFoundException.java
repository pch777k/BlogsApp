package com.pch777.blogs.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String message;

}
