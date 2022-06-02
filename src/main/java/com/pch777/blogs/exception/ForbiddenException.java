package com.pch777.blogs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="Access is denied")
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 8611933082131721018L;

	public ForbiddenException() {
		super();
	}

	public ForbiddenException(String message) {
		super(message);
	}

}
