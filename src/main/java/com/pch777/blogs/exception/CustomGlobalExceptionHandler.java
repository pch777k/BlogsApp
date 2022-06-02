package com.pch777.blogs.exception;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class CustomGlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    List<String> errors = ex
	        .getBindingResult()
	        .getFieldErrors()
	        .stream()
	        .map(x -> x.getField() + " - " + x.getDefaultMessage())
	        .collect(Collectors.toList());
	    body.put("errors", errors);
	    return new ResponseEntity<>(body, status);
	}
	
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValidException(SQLException ex) {
	    Map<String, Object> body = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    body.put("timestamp", new Date());
	    body.put("status", status.value());
	    String error = ex.getMessage();
	    body.put("error", error);
	    return new ResponseEntity<>(body, status);
	}
}
