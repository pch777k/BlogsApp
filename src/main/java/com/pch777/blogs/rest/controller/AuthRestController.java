package com.pch777.blogs.rest.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pch777.blogs.dto.RegisterUserDto;
import com.pch777.blogs.exception.ResourceNotFoundException;
import com.pch777.blogs.response.MessageResponse;
import com.pch777.blogs.service.AuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthRestController {

	private final AuthService authService;
	
	@PostMapping
	public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto userDto) throws IOException, ResourceNotFoundException {
		if(authService.isUsernameExists(userDto.getUsername())) {
			return ResponseEntity
			          .badRequest()
			          .body(new MessageResponse("Error: Username is already taken!"));
		}
		authService.signup(userDto);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
}
